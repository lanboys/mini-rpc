package com.mini.rpc.common.exception.api;

import com.mini.rpc.common.base.ApiStatus;

public class ParameterException extends ApiStatusException {

  public ParameterException() {
    super(ApiStatus.PARAMETER);
  }

  public ParameterException(Throwable cause) {
    super(ApiStatus.PARAMETER, cause);
  }

  public ParameterException(String message) {
    super(ApiStatus.PARAMETER, "参数异常: " + message);
  }
}
