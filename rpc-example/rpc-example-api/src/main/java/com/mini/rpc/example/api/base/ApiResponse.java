package com.mini.rpc.example.api.base;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ApiResponse<T> implements Serializable {

  private long code;
  private String msg;
  private T data;

  private ApiResponse() {
  }

  public static <T> ApiResponse<T> ok() {
    return status(ApiStatus.SUCCESS);
  }

  public static <T> ApiResponse<T> error() {
    return status(ApiStatus.FAILED);
  }

  public static <T> ApiResponse<T> status(ApiStatus apiStatus) {
    return customize(apiStatus.getCode(), apiStatus.getMsg());
  }

  public static <T> ApiResponse<T> customize(long code, String msg) {
    ApiResponse<T> r = new ApiResponse<>();
    r.code = code;
    r.msg = msg;
    return r;
  }

  public long getCode() {
    return code;
  }

  public String getMsg() {
    return msg;
  }

  /**
   * 不能为空
   */
  public ApiResponse<T> data(T data) {
    if (data == null) {
      return status(ApiStatus.DATA_NULL);
    }
    this.data = data;
    return this;
  }

  /**
   * 可以为空
   */
  public ApiResponse<T> dataNullable(T data) {
    this.data = data;
    return this;
  }

  public ApiResponse<T> msg(String msg) {
    this.msg = msg;
    return this;
  }

  public ApiResponse<T> code(long code) {
    this.code = code;
    return this;
  }

  private boolean isSuccess() {
    return ApiStatus.SUCCESS.getCode() == getCode() || ApiStatus.DATA_NULL.getCode() == getCode();
  }

  /**
   * 是否有返回实际的数据
   */
  private boolean isPresent() {
    return data != null;
  }

  public T presentOrElseThrow() {
    if (isSuccess() && isPresent()) {
      return data;
    }
    throw isSuccess() ? toNullDataException() : toException();
  }

  public T successOrElseThrow() {
    if (isSuccess()) {
      return data;
    }
    throw toException();
  }

  private ApiStatusException toException() {
    ApiStatus apiStatus = ApiStatus.fromCode(getCode());
    return new ApiStatusException(apiStatus, getMsg());
  }

  private ApiStatusException toNullDataException() {
    return new NullDataException();
  }

  public Map<String, Object> toMap() {
    Map<String, Object> map = new HashMap<>();
    map.put("code", code);
    map.put("msg", msg);
    map.put("data", data);
    return map;
  }
}
