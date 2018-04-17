package com.cxt.fly.registry;

/**
 * Created by Administrator on 2018/1/22.
 */
public interface ServiceDiscover {
    /**
     *
     * @param serviceName
     * @return
     */
    String discover(String serviceName);
}
