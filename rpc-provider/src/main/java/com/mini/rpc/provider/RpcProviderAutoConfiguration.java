package com.mini.rpc.provider;

import com.mini.rpc.common.RpcProperties;
import com.mini.rpc.provider.registry.RegistryFactory;
import com.mini.rpc.provider.registry.RegistryService;
import com.mini.rpc.provider.registry.RegistryType;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

@Configuration
@EnableConfigurationProperties(RpcProperties.class)
public class RpcProviderAutoConfiguration {

    @Resource
    private RpcProperties rpcProperties;

    @Bean
    public RpcProvider rpcProvider() throws Exception {
        if (StringUtils.isEmpty(rpcProperties.getRegistryAddr())) {
            throw new RuntimeException("please config registry address like : rpc.registryAddr=127.0.0.1:2181");
        }

        if (StringUtils.isEmpty(rpcProperties.getRegistryType())) {
            throw new RuntimeException("please config registry type like : rpc.registryType=ZOOKEEPER");
        }

        if (0 == rpcProperties.getServicePort()) {
            throw new RuntimeException("please config service port like : rpc.servicePort=8888");
        }
        RegistryType type = RegistryType.valueOf(rpcProperties.getRegistryType());
        RegistryService serviceRegistry = RegistryFactory.getInstance(rpcProperties.getRegistryAddr(), type);
        return new RpcProvider(rpcProperties.getServicePort(), serviceRegistry);
    }
}
