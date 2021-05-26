package com.mini.rpc.example.api.base;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiStatusException extends BaseException {

  private ApiStatus apiStatus;

  public ApiStatusException(ApiStatus apiStatus) {
    super(apiStatus.getMsg());
    this.apiStatus = apiStatus;
  }

  public ApiStatusException(ApiStatus apiStatus, Throwable cause) {
    super(apiStatus.getMsg(), cause);
    this.apiStatus = apiStatus;
  }

  public ApiStatusException(ApiStatus apiStatus, String message) {
    super(message);
    this.apiStatus = apiStatus;
  }

  public ApiStatus getApiStatus() {
    return apiStatus;
  }

  public void setApiStatus(ApiStatus apiStatus) {
    this.apiStatus = apiStatus;
  }
}
