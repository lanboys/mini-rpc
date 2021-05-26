package com.mini.rpc.common;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "rpc")
public class RpcProperties {

    private int servicePort;

    private String registryAddr;

    private String registryType;

    /**
     * 空闲连接读超时时间，单位秒
     */
    private long readerIdleTime = 60;
}
