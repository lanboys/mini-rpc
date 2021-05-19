package com.mini.rpc.example.consumer.controller;

import com.mini.rpc.consumer.annotation.RpcReference;
import com.mini.rpc.example.api.HelloService;
import com.mini.rpc.example.api.WorldService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

  @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection", "SpringJavaInjectionPointsAutowiringInspection"})
  @RpcReference(serviceVersion = "1.0.0", timeout = 3000)
  private HelloService helloFacade;

  @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection", "SpringJavaInjectionPointsAutowiringInspection"})
  @RpcReference(serviceVersion = "1.0.0", timeout = 3000)
  private WorldService worldFacade;

  @RequestMapping(value = "/hello", method = RequestMethod.GET)
  public String sayHello() {
    return helloFacade.hello("mini rpc");
  }

  @RequestMapping(value = "/world", method = RequestMethod.GET)
  public String sayWorld() {
    return worldFacade.world("mini rpc");
  }
}
