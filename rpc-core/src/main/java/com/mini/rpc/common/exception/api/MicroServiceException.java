package com.mini.rpc.common.exception.api;

import com.mini.rpc.common.base.ApiStatus;

import static com.mini.rpc.common.base.ApiStatus.MICRO_SERVICE_ERROR;

public class MicroServiceException extends ApiStatusException {

  public MicroServiceException() {
    super(MICRO_SERVICE_ERROR);
  }

  public MicroServiceException(ApiStatus apiStatus) {
    super(apiStatus);
  }

  public MicroServiceException(ApiStatus apiStatus, Throwable cause) {
    super(apiStatus, cause);
  }

  public MicroServiceException(ApiStatus apiStatus, String message) {
    super(apiStatus, message);
  }
}
