package com.cxt.fly.client;

import com.cxt.fly.model.RpcRequest;
import com.cxt.fly.model.RpcResponse;
import com.cxt.fly.registry.ServiceDiscover;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * rpc 客户端代理
 * 用动态代理创建代理对象，代理对象执行网络请求返回响应内容
 * @author cxt
 * @date 2018/3/29
 */
public class RpcProxy {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcProxy.class);

    private String serviceAddress;
    private ServiceDiscover serviceDiscover;

    public RpcProxy(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public RpcProxy(ServiceDiscover serviceDiscover) {
        this.serviceDiscover = serviceDiscover;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(final Class<T> interfaceClass) {
        return create(interfaceClass, "");
    }

    @SuppressWarnings("unchecked")
    public <T> T create(final Class<T> interfaceClass, final String version) {
        /*创建动态代理对象*/
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        /*创建RPC请求*/
                        RpcRequest rpcRequest = RpcRequest.build(method.getDeclaringClass().getName(), version, method.getName(), method.getParameterTypes(), args);

                        if(null != serviceDiscover){
                            String serviceName = interfaceClass.getName();
                            if(StringUtils.isNotEmpty(version)){
                                serviceName += version;
                            }
                            serviceAddress = serviceDiscover.discover(serviceName);
                            if (StringUtils.isEmpty(serviceAddress)){
                                throw new RuntimeException("can not a service addresss");
                            }
                            LOGGER.debug("find a service :" + serviceName + " from " + serviceAddress);

                            /*分割字符串serviceAddress,如127.0.0.1:8080 分割为 127.0.0.1和8080*/
                            String[] hostAndPort = serviceAddress.split(":");
                            RpcClient rpcClient = new RpcClient(hostAndPort[0],Integer.parseInt(hostAndPort[1]));

                            RpcResponse rpcResponse = rpcClient.send(rpcRequest);

                            if (null != rpcResponse){
                                if(rpcResponse.getException() != null){
                                    throw rpcResponse.getException();
                                }
                                else {
                                    return rpcResponse.getResult();
                                }
                            }else {
                                throw new RuntimeException("response is null");
                            }
                        }

                        throw new RuntimeException("serviceDiscover is null");
                    }
                });
    }
}
