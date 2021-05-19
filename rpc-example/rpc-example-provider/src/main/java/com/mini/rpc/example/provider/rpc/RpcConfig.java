package com.mini.rpc.example.provider.rpc;

import com.mini.rpc.provider.RpcProviderAutoConfiguration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({RpcProviderAutoConfiguration.class})
@ConditionalOnMissingClass("org.springframework.boot.test.context.SpringBootTest")
public class RpcConfig {

}
