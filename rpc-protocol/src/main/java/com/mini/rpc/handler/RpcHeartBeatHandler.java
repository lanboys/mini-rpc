package com.mini.rpc.handler;

import com.mini.rpc.common.MiniRpcHeartBeat;
import com.mini.rpc.common.MiniRpcRequestHolder;
import com.mini.rpc.protocol.MiniRpcProtocol;
import com.mini.rpc.protocol.MsgHeader;
import com.mini.rpc.protocol.MsgType;
import com.mini.rpc.protocol.ProtocolConstants;
import com.mini.rpc.serialization.SerializationTypeEnum;

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcHeartBeatHandler extends ChannelInboundHandlerAdapter {

  private static final int HEARTBEAT_INTERVAL = 10;

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    super.channelActive(ctx);
    doHeartBeatTask(ctx);
  }

  private void doHeartBeatTask(ChannelHandlerContext ctx) {
    ctx.executor().schedule(() -> {
      if (ctx.channel().isActive()) {
        MiniRpcProtocol<MiniRpcHeartBeat> heartBeatData = buildHeartBeatData();
        log.info("发送心跳数据：{}", heartBeatData);
        ctx.channel().writeAndFlush(heartBeatData);
        doHeartBeatTask(ctx);
      } else {
        log.info("已关闭连接，不再发送心跳数据");
      }
    }, HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
  }

  private MiniRpcProtocol<MiniRpcHeartBeat> buildHeartBeatData() {
    long requestId = MiniRpcRequestHolder.REQUEST_ID_GEN.incrementAndGet();

    MsgHeader header = new MsgHeader();
    header.setMagic(ProtocolConstants.MAGIC);
    header.setVersion(ProtocolConstants.VERSION);
    header.setRequestId(requestId);
    header.setSerialization((byte) SerializationTypeEnum.HESSIAN.getType());
    header.setMsgType((byte) MsgType.HEARTBEAT.getType());
    header.setStatus((byte) 0x1);

    MiniRpcHeartBeat request = new MiniRpcHeartBeat();

    MiniRpcProtocol<MiniRpcHeartBeat> protocol = new MiniRpcProtocol<>();
    protocol.setHeader(header);
    protocol.setBody(request);
    return protocol;
  }
}