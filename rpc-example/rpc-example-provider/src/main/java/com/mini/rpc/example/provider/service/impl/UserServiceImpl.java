package com.mini.rpc.example.provider.service.impl;

import com.mini.rpc.example.api.base.ApiRequest;
import com.mini.rpc.example.api.base.ApiResponse;
import com.mini.rpc.example.api.entity.User;
import com.mini.rpc.example.api.service.UserService;
import com.mini.rpc.provider.annotation.RpcService;

@RpcService(serviceInterface = UserService.class, serviceVersion = "1.0.0")
public class UserServiceImpl implements UserService {

  @Override
  public User addUser(User user) {
    user.setId(1L);
    return user;
  }

  @Override
  public ApiResponse<User> addUserRequest(ApiRequest<User> apiRequest) {
    User user = apiRequest.getData();
    user.setId(2L);
    return ApiResponse.<User>ok().data(user);
  }
}
