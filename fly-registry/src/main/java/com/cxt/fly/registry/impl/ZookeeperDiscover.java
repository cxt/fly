package com.cxt.fly.registry.impl;

import com.cxt.fly.common.Constants;
import com.cxt.fly.registry.ServiceDiscover;
import io.netty.util.internal.ThreadLocalRandom;
import org.I0Itec.zkclient.ZkClient;

import java.util.List;

/**
 * zk service discover
 * @author cxt
 * @date 2018/1/23
 */
public class ZookeeperDiscover implements ServiceDiscover {
    private String zkAddress;

    /**
     *
     * @param zkAddress  zookeeper地址
     */
    public ZookeeperDiscover(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    @Override
    public String discover(String serviceName) {
        /* create a  zk client */
        ZkClient zkClient = new ZkClient(zkAddress, Constants.ZK_SESSION_TIMEOUT,Constants.ZK_CONNECTION_TIMEOUT);
        try {
            String servicePath = Constants.ZK_REGISTRY_PATH + "/" + serviceName;
            if (!zkClient.exists(servicePath)){
                throw new RuntimeException("can not find service node: " + servicePath);
            }

            List<String> children = zkClient.getChildren(servicePath);
            if (null == children || children.size() <= 0){
                throw new RuntimeException("not has " + serviceName);
            }

            String address;

            if(children.size() == 1) {
                address = children.get(0);
            }else {
                address = children.get(ThreadLocalRandom.current().nextInt(children.size()));
            }

            String addressPath = servicePath + "/" + address;

            return zkClient.readData(addressPath);
        }finally {
            zkClient.close();
        }
    }
}
