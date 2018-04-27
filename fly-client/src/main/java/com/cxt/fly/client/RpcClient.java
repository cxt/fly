package com.cxt.fly.client;


import com.cxt.fly.codec.*;
import com.cxt.fly.model.RpcRequest;
import com.cxt.fly.model.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * RpcClient
 * @author cxt
 * @date   2018/3/29
 */
public class RpcClient extends SimpleChannelInboundHandler<RpcResponse> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);
    private final String host;
    private final Integer port;
    private final String serializeType;
    private RpcResponse response;

    public RpcClient(String host, Integer port, String serializeType) {
        this.host = host;
        this.port = port;
        this.serializeType = serializeType;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        this.response = msg;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("clien catch a exception",cause);
        ctx.close();
    }

    /**
     * 发送请求
     * @param request
     * @return
     * @throws InterruptedException
     */
    public RpcResponse send(RpcRequest request) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            ByteToMessageDecoder decoder = null;
                            MessageToByteEncoder encoder = null;
                            /*选择序列化框架*/
                            switch (serializeType) {
                                case "kryo":
                                    encoder = new RpcKryoEncoder(RpcRequest.class);
                                    decoder = new RpcKryoDecoder(RpcResponse.class);
                                    break;
                                case "hessian":
                                    encoder = new RpcHessianEncoder(RpcRequest.class);
                                    decoder = new RpcHessianDecoder(RpcResponse.class);
                                    break;
                                case "fastjson":
                                    encoder = new RpcFastjsonEncoder(RpcRequest.class);
                                    decoder = new RpcFastjsonDecoder(RpcResponse.class);
                                    break;
                                default:
                                    encoder = new RpcProtostuffEncoder(RpcRequest.class);
                                    decoder = new RpcProtostuffDecoder(RpcResponse.class);
                            }
                            pipeline.addLast(encoder)
                                    .addLast(decoder)
                                    .addLast(RpcClient.this);
                        }
                    });

            bootstrap.option(ChannelOption.TCP_NODELAY,true);
            ChannelFuture future = bootstrap.connect(host, port).sync();
            Channel channel = future.channel();
            channel.writeAndFlush(request).sync();
            channel.closeFuture().sync();

            return response;
        }finally {
            group.shutdownGracefully();
        }
    }
}
