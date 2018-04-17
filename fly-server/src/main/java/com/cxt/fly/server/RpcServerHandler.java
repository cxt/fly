package com.cxt.fly.server;

import com.cxt.fly.model.RpcRequest;
import com.cxt.fly.model.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.EventExecutorGroup;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Administrator
 * @date   2018/3/8
 */
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServerHandler.class);

    private Map<String,Object> serviceBeanMap;

    public RpcServerHandler(Map<String,Object> serviceBeanMap){
        this.serviceBeanMap = serviceBeanMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        /* 处理RPC请求并且返回RPC响应 */
        RpcResponse response = new RpcResponse();
        response.setRequestId(msg.getRequestId());
        try {
            Object result = handle(msg);
            response.setResult(result);
        }catch (Exception e){
            LOGGER.error("handle request error",e);
            response.setException(e);
        }
        /* 写入RPC响应 */
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private Object handle(RpcRequest rpcRequest)throws Exception{
        /* 拿出request里面的信息*/
        String serviceName = rpcRequest.getServiceName();
        String version = rpcRequest.getVersion();
        String methodName = rpcRequest.getMethod();
        Class<?>[] paramTypes = rpcRequest.getParamTypes();
        Object[] params = rpcRequest.getParams();

        if(StringUtils.isNotEmpty(version)){
            serviceName += version;
        }
        Object serviceBean = serviceBeanMap.get(serviceName);
        Class<?> serviceClass = serviceBean.getClass();

        // 执行反射方法调用
        Method method = serviceClass.getMethod(methodName, paramTypes);
        method.setAccessible(true);
        Object result = method.invoke(serviceBean, params);

        return result;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("server catch exception.",cause);
        ctx.close();
    }
}
