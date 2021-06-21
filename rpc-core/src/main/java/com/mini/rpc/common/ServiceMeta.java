package com.mini.rpc.common;

import org.codehaus.jackson.annotate.JsonIgnore;

import lombok.Data;

@Data
public class ServiceMeta {

  private String serviceName;

  private String serviceVersion;

  private String serviceAddr;

  private String localServiceAddr;

  private int servicePort;

  @JsonIgnore
  private Object actualService;

  @JsonIgnore
  private Class<?> serviceClass;
}
