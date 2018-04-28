package com.cxt.fly.service.impl;

import com.cxt.fly.model.User;
import com.cxt.fly.server.RpcService;
import com.cxt.fly.service.HelloWorldService2;

/**
 * HelloWorldService2Impl
 *
 * @author cxt
 * @date 2018/4/17
 */
@RpcService(value = HelloWorldService2.class, version = "helloworld2")
public class HelloWorldService2Impl implements HelloWorldService2 {

    @Override
    public String say(User user) {
        return "fuck " + user.getName() + "whose age is " + user.getAge();
    }
}
