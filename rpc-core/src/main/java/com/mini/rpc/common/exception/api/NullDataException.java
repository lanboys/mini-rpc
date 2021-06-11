package com.mini.rpc.common.exception.api;

import com.mini.rpc.common.base.ApiStatus;

public class NullDataException extends ApiStatusException {

  public NullDataException() {
    super(ApiStatus.DATA_NULL);
  }

  public NullDataException(Throwable cause) {
    super(ApiStatus.DATA_NULL, cause);
  }
}
