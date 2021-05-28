package com.mini.rpc.codec;

import com.mini.rpc.common.MiniRpcRequest;
import com.mini.rpc.common.MiniRpcResponse;
import com.mini.rpc.protocol.MiniRpcProtocol;
import com.mini.rpc.protocol.MsgHeader;
import com.mini.rpc.serialization.RpcSerialization;
import com.mini.rpc.serialization.SerializationFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MiniRpcEncoder extends MessageToByteEncoder<MiniRpcProtocol<Object>> {

    /*
    +---------------------------------------------------------------+
    | 魔数 2byte | 协议版本号 1byte | 序列化算法 1byte | 报文类型 1byte  |
    +---------------------------------------------------------------+
    | 状态 1byte |        消息 ID 8byte     |      数据长度 4byte     |
    +---------------------------------------------------------------+
    |                   数据内容 （长度不定）                          |
    +---------------------------------------------------------------+
    */
    @Override
    protected void encode(ChannelHandlerContext ctx, MiniRpcProtocol<Object> msg, ByteBuf byteBuf) throws Exception {
        MsgHeader header = msg.getHeader();
        byteBuf.writeShort(header.getMagic());
        byteBuf.writeByte(header.getVersion());
        byteBuf.writeByte(header.getSerialization());
        byteBuf.writeByte(header.getMsgType());
        byteBuf.writeByte(header.getStatus());
        byteBuf.writeLong(header.getRequestId());
        RpcSerialization rpcSerialization = SerializationFactory.getRpcSerialization(header.getSerialization());
        Object body = msg.getBody();
        if (body instanceof MiniRpcResponse) {
            Object oldData = ((MiniRpcResponse) body).getData();
            if (oldData != null) {
                String data = rpcSerialization.serializationString(oldData);
                ((MiniRpcResponse) body).setData(data);
            }
        } else if (body instanceof MiniRpcRequest) {
            Object[] oldParams = ((MiniRpcRequest) body).getParams();
            if (oldParams != null&&oldParams.length>0) {
                String[] params = rpcSerialization.serializationString(oldParams);
                ((MiniRpcRequest) body).setParams(params);
            }
        }
        byte[] data = rpcSerialization.serialize(body);
        byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);
    }
}
