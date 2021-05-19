package com.mini.rpc.example.provider.service.impl;

import com.mini.rpc.example.api.WorldService;
import com.mini.rpc.provider.annotation.RpcService;

@RpcService(serviceInterface = WorldService.class, serviceVersion = "1.0.0")
public class WorldServiceImpl implements WorldService {

  @Override
  public String world(String name) {
    return "world " + name;
  }
}
