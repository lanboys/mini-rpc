package com.mini.rpc.provider.facade;

import com.mini.rpc.provider.annotation.RpcService;

@RpcService(serviceInterface = WorldFacade.class, serviceVersion = "1.0.0")
public class WorldFacadeImpl implements WorldFacade {
    @Override
    public String world(String name) {
        return "world " + name;
    }
}
