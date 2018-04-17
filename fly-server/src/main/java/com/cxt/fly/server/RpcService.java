package com.cxt.fly.server;

import org.springframework.stereotype.Component;
import sun.util.resources.cldr.ss.CalendarData_ss_SZ;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务类的注解
 * @author Administrator
 * @date   2018/3/8
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {
    /**
     * 服务接口
     * @return
     */
    Class<?> value();

    /**
     * service version
     * @return
     */
    String version() default "";
}
