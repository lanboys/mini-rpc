package com.mini.rpc.common.exception;

public class BaseException extends RuntimeException {

  public BaseException(Throwable cause) {
    super(cause);
  }

  public BaseException(String message) {
    super(message);
  }

  public BaseException(String message, Throwable cause) {
    super(message, cause);
  }
}
