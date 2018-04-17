package com.cxt.fly.service.impl;

import com.cxt.fly.server.RpcService;
import com.cxt.fly.service.HelloWorldService;
import com.cxt.fly.service.HelloWorldService2;

/**
 * HelloWorldService2Impl
 * @author cxt
 * @date   2018/4/17
 */
@RpcService(value = HelloWorldService.class,version = "helloworld2")
public class HelloWorldService2Impl implements HelloWorldService2 {
    @Override
    public String say(String username) {
        return "fuck " + username;
    }
}
