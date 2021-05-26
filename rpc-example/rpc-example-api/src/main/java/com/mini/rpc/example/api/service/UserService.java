package com.mini.rpc.example.api.service;

import com.mini.rpc.example.api.base.ApiRequest;
import com.mini.rpc.example.api.base.ApiResponse;
import com.mini.rpc.example.api.entity.User;

public interface UserService {

  public User addUser(User user);

  public ApiResponse<User> addUserRequest(ApiRequest<User> apiRequest);
}
