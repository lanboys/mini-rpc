package com.mini.rpc.example.consumer;

import com.mini.rpc.consumer.RpcConsumerAutoConfiguration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({RpcConsumerAutoConfiguration.class})
public class RpcConsumerApplication {

  public static void main(String[] args) {
    SpringApplication.run(RpcConsumerApplication.class, args);
  }
}
