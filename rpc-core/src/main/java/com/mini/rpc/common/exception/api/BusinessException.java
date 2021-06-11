package com.mini.rpc.common.exception.api;

import com.mini.rpc.common.base.ApiStatus;

public class BusinessException extends ApiStatusException {

  public BusinessException() {
    super(ApiStatus.BUSINESS);
  }

  public BusinessException(Throwable cause) {
    super(ApiStatus.BUSINESS, cause);
  }

  public BusinessException(String message) {
    super(ApiStatus.BUSINESS, message);
  }
}
