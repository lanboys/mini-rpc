package com.mini.rpc.consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RpcConsumerAutoConfiguration {

  @Bean
  public RpcConsumerPostProcessor rpcConsumerPostProcessor() {
    return new RpcConsumerPostProcessor();
  }
}
