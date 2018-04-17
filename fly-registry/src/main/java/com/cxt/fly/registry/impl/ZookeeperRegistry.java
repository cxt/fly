package com.cxt.fly.registry.impl;


import com.cxt.fly.common.Constants;
import com.cxt.fly.registry.ServiceRegistry;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cxt
 * @date 2018/1/22.
 */
public class ZookeeperRegistry implements ServiceRegistry {
    private final ZkClient zkClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperRegistry.class);

    public ZookeeperRegistry(String zkAddress){
        /* create a  zk client */
        zkClient = new ZkClient(zkAddress, Constants.ZK_SESSION_TIMEOUT,Constants.ZK_CONNECTION_TIMEOUT);
    }

    @Override
    public void register(String serviceName, String serviceAddress) {
        String registryPath = Constants.ZK_REGISTRY_PATH;
        if(!zkClient.exists(registryPath)){
            zkClient.createPersistent(registryPath);
        }

        String servicePath = registryPath + "/" +serviceName;
        if(!zkClient.exists(servicePath)){
            zkClient.createPersistent(servicePath);
            LOGGER.debug("create zk node " + servicePath);
        }

        String addressPath = servicePath + "/address-";
        String ephemeralSequential = zkClient.createEphemeralSequential(addressPath, serviceAddress);
        LOGGER.debug("create zk node " + ephemeralSequential);
    }
}
