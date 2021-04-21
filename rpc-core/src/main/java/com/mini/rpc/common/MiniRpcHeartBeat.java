package com.mini.rpc.common;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class MiniRpcHeartBeat implements Serializable {

  private Date date = new Date();
}

