package com.mini.rpc.example.provider.service.impl;

import com.mini.rpc.example.api.base.ApiRequest;
import com.mini.rpc.example.api.base.ApiResponse;
import com.mini.rpc.example.api.entity.User;
import com.mini.rpc.example.api.service.UserService;
import com.mini.rpc.provider.annotation.RpcService;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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

  @Override
  public void executeJob() {
    log.info("job executing...");
  }

  @Override
  public User findMaxIdUser() {
    User user = new User();
    user.setId(1000L);
    user.setName("bob");
    user.setAge(15);
    user.setEmail("bob@163.com");
    user.setBirthday(LocalDate.now());
    user.setRegisterTime(LocalDateTime.now());
    return user;
  }

  @Override
  public ApiResponse<ApiResponse<User>> testType(ApiRequest<User> apiRequest1, ApiRequest<ApiRequest<User>> apiRequest2) {
    User user = apiRequest2.getData().getData();
    user.setId(2L);
    ApiResponse<User> data = ApiResponse.<User>ok().data(user);

    ApiResponse<ApiResponse<User>> response = ApiResponse.<ApiResponse<User>>ok().data(data);
    return response;
  }
}
