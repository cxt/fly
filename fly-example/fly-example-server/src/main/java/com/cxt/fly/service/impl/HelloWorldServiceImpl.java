package com.cxt.fly.service.impl;

import com.cxt.fly.server.RpcService;
import com.cxt.fly.service.HelloWorldService;

/**
 * HelloWorldServiceImpl
 * @author cxt
 * @date   2018/4/17
 */
@RpcService(value = HelloWorldService.class)
public class HelloWorldServiceImpl implements HelloWorldService {
    @Override
    public String say(String username) {
        return "hello " + username;
    }
}
