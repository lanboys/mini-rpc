package com.mini.rpc.handler;

import com.mini.rpc.common.MiniRpcRequest;
import com.mini.rpc.common.MiniRpcResponse;
import com.mini.rpc.common.RpcServiceHelper;
import com.mini.rpc.protocol.MiniRpcProtocol;
import com.mini.rpc.protocol.MsgHeader;
import com.mini.rpc.protocol.MsgStatus;
import com.mini.rpc.protocol.MsgType;
import com.mini.rpc.serialization.RpcSerialization;
import com.mini.rpc.serialization.SerializationFactory;

import org.springframework.cglib.reflect.FastClass;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcRequestHandler extends SimpleChannelInboundHandler<MiniRpcProtocol<MiniRpcRequest>> {

    private final Map<String, Object> rpcServiceMap;

    public RpcRequestHandler(Map<String, Object> rpcServiceMap) {
        this.rpcServiceMap = rpcServiceMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MiniRpcProtocol<MiniRpcRequest> protocol) {
        RpcRequestProcessor.submitRequest(() -> {
            MiniRpcProtocol<MiniRpcResponse> resProtocol = new MiniRpcProtocol<>();
            MiniRpcResponse response = new MiniRpcResponse();
            MsgHeader header = protocol.getHeader();
            header.setMsgType((byte) MsgType.RESPONSE.getType());
            try {
                MiniRpcRequest body = protocol.getBody();
                Object result = handle(SerializationFactory.getRpcSerialization(header.getSerialization()), body);
                response.setData(result);
                header.setStatus((byte) MsgStatus.SUCCESS.getCode());
            } catch (Throwable throwable) {
                response.setMessage(throwable.toString());
                header.setStatus((byte) MsgStatus.FAIL.getCode());
                log.error("process request {} error", header.getRequestId(), throwable);
            }
            resProtocol.setHeader(header);
            resProtocol.setBody(response);
            ctx.channel().writeAndFlush(resProtocol);
        });
    }

    private Object handle(RpcSerialization rpcSerialization, MiniRpcRequest request) throws Throwable {
        String serviceKey = RpcServiceHelper.buildServiceKey(request.getClassName(), request.getServiceVersion());
        Object serviceBean = rpcServiceMap.get(serviceKey);

        if (serviceBean == null) {
            throw new RuntimeException(String.format("service not exist: %s:%s", request.getClassName(), request.getMethodName()));
        }

        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();

        Method method = serviceClass.getMethod(methodName, parameterTypes);
        Type[] types = method.getGenericParameterTypes();
        Object[] params = request.getParams();
        Object[] parameters = null;
        if (params != null && params.length > 0) {
            parameters = rpcSerialization.deserialize(params, types);
        }
        FastClass fastClass = FastClass.create(serviceClass);
        int methodIndex = fastClass.getIndex(methodName, parameterTypes);
        return fastClass.invoke(methodIndex, serviceBean, parameters);
    }
}
