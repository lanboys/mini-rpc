package com.mini.rpc.handler;

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcIdleStateHandler extends IdleStateHandler {

  private static final long READER_IDLE_TIME = 60;

  public RpcIdleStateHandler() {
    super(READER_IDLE_TIME, 0, 0, TimeUnit.SECONDS);
  }

  public RpcIdleStateHandler(long readerIdleTime) {
    super(readerIdleTime, 0, 0, TimeUnit.SECONDS);
  }

  @Override
  protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) {
    log.info("{}秒内未读到数据，关闭连接", READER_IDLE_TIME);
    ctx.channel().close();
  }
}