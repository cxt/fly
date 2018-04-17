package com.cxt.fly.registry;

/**
 * 服务注册接口
 * @author cxt
 * @date 2018/3/10
 */
public interface ServiceRegistry {
    /**
     * 注册服务
     * @param serviceName
     * @param serviceAddress
     */
    void register(String serviceName, String serviceAddress);
}
