package com.mini.rpc.consumer.annotation;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Autowired
public @interface RpcReference {

    String serviceVersion() default "1.0";

    String registryType() default "ZOOKEEPER";

    String registryAddress() default "127.0.0.1:2181";

    /**
     * 请求超时时间 单位ms
     */
    long timeout() default 5000;

    /**
     * 向服务端发送心跳的间隔时间 单位s
     */
    long heartbeatInterval() default 10;

}
