package com.mini.rpc.example.api.base;

public class NullDataException extends ApiStatusException {

  public NullDataException() {
    super(ApiStatus.DATA_NULL);
  }

  public NullDataException(Throwable cause) {
    super(ApiStatus.DATA_NULL, cause);
  }
}
