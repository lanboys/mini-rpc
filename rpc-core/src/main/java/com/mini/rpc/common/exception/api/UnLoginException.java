package com.mini.rpc.common.exception.api;

import com.mini.rpc.common.base.ApiStatus;

public class UnLoginException extends WebSecurityException {

  public UnLoginException() {
    super(ApiStatus.LOGIN_INVALID);
  }

  public UnLoginException(Throwable cause) {
    super(ApiStatus.LOGIN_INVALID, cause);
  }
}