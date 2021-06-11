package com.mini.rpc.common.exception.api;

import com.mini.rpc.common.base.ApiStatus;

public class ForbiddenException extends WebSecurityException {

  public ForbiddenException() {
    super(ApiStatus.FORBIDDEN);
  }

  public ForbiddenException(Throwable cause) {
    super(ApiStatus.FORBIDDEN, cause);
  }
}