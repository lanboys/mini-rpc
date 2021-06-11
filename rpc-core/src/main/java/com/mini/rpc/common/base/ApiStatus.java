package com.mini.rpc.common.base;

public enum ApiStatus {

  SUCCESS(2000, "请求成功"),
  PARAMETER(4000, "参数异常"),
  FAILED(5000, "系统繁忙!"),
  BUSINESS(6000, "业务异常"),
  DATA_ERROR(7000, "数据异常"),
  DATA_NULL(7001, "暂无数据"),
  NOT_FOUND(4004, "无效的接口地址"),
  INVALID_USER(1000, "账号或者密码错误"),
  LOGIN_INVALID(1010, "未登录或长时间未登录"),
  LOGIN_OTHER(1011, "你已经在他处登录，退出当前系统"),
  DISABLE(1020, "账户已被禁用"),
  FORBIDDEN(1030, "无权限访问"),
  ILLEGAL_REQUEST(1040, "非法请求"),
  MICRO_SERVICE_ERROR(8000, "微服务异常, 请稍后再试~~"),
  MICRO_SERVICE_TIME_OUT(8001, "微服务超时, 请稍后再试~~"),
  MICRO_SERVICE_NOT_AVAILABLE(8002, "微服务不可用, 请稍后再试~~"),
  ;

  private final long code;
  private final String msg;

  ApiStatus(final long code, final String msg) {
    this.code = code;
    this.msg = msg;
  }

  public static ApiStatus fromCode(long code) {
    ApiStatus[] ecs = ApiStatus.values();
    for (ApiStatus ec : ecs) {
      if (ec.getCode() == code) {
        return ec;
      }
    }
    return null;
  }

  public long getCode() {
    return code;
  }

  public String getMsg() {
    return msg;
  }

  @Override
  public String toString() {
    return String.format(" status:{code=%s, msg=%s} ", code, msg);
  }
}
