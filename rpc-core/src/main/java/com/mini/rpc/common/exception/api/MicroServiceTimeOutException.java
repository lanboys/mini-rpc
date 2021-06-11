package com.mini.rpc.common.exception.api;

import static com.mini.rpc.common.base.ApiStatus.MICRO_SERVICE_TIME_OUT;

public class MicroServiceTimeOutException extends MicroServiceException {

  public MicroServiceTimeOutException() {
    super(MICRO_SERVICE_TIME_OUT);
  }

  public MicroServiceTimeOutException(Throwable cause) {
    super(MICRO_SERVICE_TIME_OUT, cause);
  }
}
