package com.mini.rpc.example.provider;

import com.mini.rpc.provider.RpcProviderAutoConfiguration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * 也可以通过注解ConditionalOnMissingClass来控制测试用例中不启动rpc, eg:
 *
 * @Configuration
 * @Import({RpcProviderAutoConfiguration.class})
 * @ConditionalOnMissingClass("org.springframework.boot.test.context.SpringBootTest") <p>
 * <p>
 * public class RpcConfig {
 * }
 */
@SpringBootApplication
@Import({RpcProviderAutoConfiguration.class})
public class RpcProviderApplication {

  public static void main(String[] args) {
    SpringApplication.run(RpcProviderApplication.class, args);
  }
}