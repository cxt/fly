package com.cxt.fly.server;

import com.cxt.fly.codec.*;
import com.cxt.fly.model.RpcRequest;
import com.cxt.fly.model.RpcResponse;
import com.cxt.fly.registry.impl.ZookeeperRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cxt
 * @date 2018/3/8
 */
public class RpcServer implements ApplicationContextAware, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    /**
     * service address
     */
    private String serviceAddresss;
    /**
     * zookeeper registry
     */
    private ZookeeperRegistry zookeeperRegistry;
    /**
     * serialize type, default use protostuff
     * */
    private String serializeType;
    /**
     * service bean map
     */
    private Map<String, Object> serviceBeansMap = new HashMap<String, Object>();

    public RpcServer(String serviceAddresss) {
        this.serviceAddresss = serviceAddresss;
    }

    public RpcServer(String serviceAddresss, ZookeeperRegistry zookeeperRegistry) {
        this.serviceAddresss = serviceAddresss;
        this.zookeeperRegistry = zookeeperRegistry;
    }

    public RpcServer(String serviceAddresss, ZookeeperRegistry zookeeperRegistry, String serializeType) {
        this.serviceAddresss = serviceAddresss;
        this.zookeeperRegistry = zookeeperRegistry;
        this.serializeType = serializeType;
    }

    /**
     * before initial bean execute
     *
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        /* initial the service bean, and put into the @serviceBeansMap */
        Map<String, Object> objectMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (MapUtils.isNotEmpty(objectMap)) {
            for (Object serviceObj : objectMap.values()) {
                /* 通过注解拿到类名和版本号 */
                RpcService rpcService = serviceObj.getClass().getAnnotation(RpcService.class);
                String serviceName = rpcService.value().getName();
                String serviceVersion = rpcService.version();

                if (StringUtils.isNotEmpty(serviceVersion)) {
                    serviceName += serviceVersion;
                }

                serviceBeansMap.put(serviceName, serviceObj);
            }
        }


    }

    /**
     * 在初始化属性后执行,创建netty bootstrap 对象
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, bossGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ByteToMessageDecoder decoder = null;
                            MessageToByteEncoder encoder = null;
                            /*选择序列化框架*/
                            switch (serializeType) {
                                case "kryo":
                                    decoder = new RpcKryoDecoder(RpcRequest.class);
                                    encoder = new RpcKryoEncoder(RpcResponse.class);
                                    break;
                                case "hessian":
                                    decoder = new RpcHessianDecoder(RpcRequest.class);
                                    encoder = new RpcHessianEncoder(RpcResponse.class);
                                    break;
                                case "fastjson":
                                    decoder = new RpcFastjsonDecoder(RpcRequest.class);
                                    encoder = new RpcFastjsonEncoder(RpcResponse.class);
                                    break;
                                default:
                                    decoder = new RpcProtostuffDecoder(RpcRequest.class);
                                    encoder = new RpcProtostuffEncoder(RpcResponse.class);
                            }

                            /*绑定解码器和编码器*/
                            ch.pipeline().addLast(decoder)
                                    .addLast(encoder)
                                    .addLast(new RpcServerHandler(serviceBeansMap));
                        }
                    });

            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

            // 获取 RPC 服务器的 IP 地址与端口号
            String[] addressArray = StringUtils.split(serviceAddresss, ":");
            String ip = addressArray[0];
            int port = Integer.parseInt(addressArray[1]);
            // 启动 RPC 服务器
            ChannelFuture future = bootstrap.bind(ip, port).sync();

            // 注册 RPC 服务地址
            if (MapUtils.isNotEmpty(serviceBeansMap)) {
                for (String serviceName : serviceBeansMap.keySet()) {
                    this.zookeeperRegistry.register(serviceName, serviceAddresss);
                }
            }

            LOGGER.debug("server started!!!");
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
