package com.mini.rpc.example.api.service;

import com.mini.rpc.common.base.ApiRequest;
import com.mini.rpc.common.base.ApiResponse;
import com.mini.rpc.example.api.entity.User;

public interface UserService {

  public User addUser(User user);

  public ApiResponse<User> addUserRequest(ApiRequest<User> apiRequest);

  public void executeJob();

  public User findMaxIdUser();

  public ApiResponse<ApiResponse<User>> testType(ApiRequest<User> apiRequest1, ApiRequest<ApiRequest<User>> apiRequest2);
}
