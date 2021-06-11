package com.mini.rpc.common.exception.api;

import static com.mini.rpc.common.base.ApiStatus.MICRO_SERVICE_NOT_AVAILABLE;

public class MicroServiceNotAvailableException extends MicroServiceException {

  public MicroServiceNotAvailableException() {
    super(MICRO_SERVICE_NOT_AVAILABLE);
  }

  public MicroServiceNotAvailableException(Throwable cause) {
    super(MICRO_SERVICE_NOT_AVAILABLE, cause);
  }
}
