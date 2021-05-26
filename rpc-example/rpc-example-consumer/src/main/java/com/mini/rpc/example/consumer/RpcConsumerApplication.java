package com.mini.rpc.example.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mini.rpc.consumer.RpcConsumerAutoConfiguration;
import com.mini.rpc.example.consumer.utils.JsonUtil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({RpcConsumerAutoConfiguration.class})
public class RpcConsumerApplication {

  public static void main(String[] args) {
    SpringApplication.run(RpcConsumerApplication.class, args);
  }

  @Bean
  public ObjectMapper objectMapper() {
    return JsonUtil.createObjectMapper();
  }
}
