package com.mini.rpc.common;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class RpcServiceHelper {

  public static String buildServiceKey(String serviceName, String serviceVersion) {
    return String.join("#", serviceName, serviceVersion);
  }

  public static HashCode hashCode(String string) {
    return Hashing.murmur3_32().hashString(string, StandardCharsets.UTF_8);
  }
}
