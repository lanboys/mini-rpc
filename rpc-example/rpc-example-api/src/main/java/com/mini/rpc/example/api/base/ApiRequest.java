package com.mini.rpc.example.api.base;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiRequest<T> implements Serializable {

  private T data;

  /**
   * 每页显示条数，默认 10
   */
  private long size = 10;

  /**
   * 当前页
   */
  private long current = 1;

  public static <T> ApiRequest<T> empty(Class<T> clazz) {
    ApiRequest<T> request = new ApiRequest<>();
    try {
      request.setData(clazz.newInstance());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return request;
  }
}
