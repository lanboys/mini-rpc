package com.mini.rpc.example.provider.service.impl;

import com.mini.rpc.example.api.HelloService;
import com.mini.rpc.provider.annotation.RpcService;

@RpcService(serviceInterface = HelloService.class, serviceVersion = "1.0.0")
public class HelloServiceImpl implements HelloService {

  @Override
  public String hello(String name) {
    return "hello " + name;
  }
}
