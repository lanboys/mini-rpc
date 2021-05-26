package com.mini.rpc.example.consumer.controller;

import com.mini.rpc.consumer.annotation.RpcReference;
import com.mini.rpc.example.api.base.ApiRequest;
import com.mini.rpc.example.api.base.ApiResponse;
import com.mini.rpc.example.api.entity.User;
import com.mini.rpc.example.api.service.HelloService;
import com.mini.rpc.example.api.service.UserService;
import com.mini.rpc.example.api.service.WorldService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
public class HelloWorldController {

  @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection", "SpringJavaInjectionPointsAutowiringInspection"})
  @RpcReference(serviceVersion = "1.0.0", timeout = 3000)
  private HelloService helloFacade;

  @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection", "SpringJavaInjectionPointsAutowiringInspection"})
  @RpcReference(serviceVersion = "1.0.0", timeout = 3000)
  private WorldService worldFacade;

  @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection", "SpringJavaInjectionPointsAutowiringInspection"})
  @RpcReference(serviceVersion = "1.0.0", timeout = 6000, heartbeatInterval = 100)
  private UserService userFacade;

  @RequestMapping(value = "/hello", method = RequestMethod.GET)
  public String sayHello() {
    return helloFacade.hello("mini rpc");
  }

  @RequestMapping(value = "/world", method = RequestMethod.GET)
  public String sayWorld() {
    return worldFacade.world("mini rpc");
  }

  @RequestMapping(value = "/addUser")
  public User addUser() {
    User user = new User();
    user.setName("bob");
    user.setAge(15);
    user.setEmail("bob@163.com");
    user.setBirthday(LocalDate.now());
    user.setRegisterTime(LocalDateTime.now());

    return userFacade.addUser(user);
  }

  @RequestMapping(value = "/addUserRequest")
  public ApiResponse<User> addUserRequest() {
    User user = new User();
    user.setName("bob");
    user.setAge(15);
    user.setEmail("bob@163.com");
    user.setBirthday(LocalDate.now());
    user.setRegisterTime(LocalDateTime.now());

    ApiRequest<User> apiRequest = new ApiRequest<>();
    apiRequest.setData(user);

    return userFacade.addUserRequest(apiRequest);
  }
}
