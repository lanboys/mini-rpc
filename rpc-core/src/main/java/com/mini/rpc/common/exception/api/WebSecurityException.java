package com.mini.rpc.common.exception.api;

import com.mini.rpc.common.base.ApiStatus;

public class WebSecurityException extends ApiStatusException {

  public WebSecurityException(ApiStatus apiStatus) {
    super(apiStatus);
  }

  public WebSecurityException(ApiStatus apiStatus, Throwable cause) {
    super(apiStatus, cause);
  }

  public WebSecurityException(ApiStatus apiStatus, String message) {
    super(apiStatus, message);
  }
}
