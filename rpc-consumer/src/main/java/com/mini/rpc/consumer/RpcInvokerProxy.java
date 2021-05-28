package com.mini.rpc.consumer;

import com.mini.rpc.common.MiniRpcFuture;
import com.mini.rpc.common.MiniRpcRequest;
import com.mini.rpc.common.MiniRpcRequestHolder;
import com.mini.rpc.common.MiniRpcResponse;
import com.mini.rpc.common.RpcServiceHelper;
import com.mini.rpc.common.ServiceMeta;
import com.mini.rpc.protocol.MiniRpcProtocol;
import com.mini.rpc.protocol.MsgHeader;
import com.mini.rpc.protocol.MsgType;
import com.mini.rpc.protocol.ProtocolConstants;
import com.mini.rpc.provider.registry.RegistryService;
import com.mini.rpc.serialization.RpcSerialization;
import com.mini.rpc.serialization.SerializationFactory;
import com.mini.rpc.serialization.SerializationTypeEnum;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;

public class RpcInvokerProxy implements InvocationHandler {

    private final String serviceVersion;
    private final long timeout;
    private long heartbeatInterval;
    private final RegistryService registryService;
    private static final Map<String, RpcConsumer> consumerMap = new ConcurrentHashMap<>();

    public RpcInvokerProxy(String serviceVersion, long timeout, long heartbeatInterval, RegistryService registryService) {
        this.serviceVersion = serviceVersion;
        this.timeout = timeout;
        this.registryService = registryService;
        this.heartbeatInterval = heartbeatInterval;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long requestId = MiniRpcRequestHolder.REQUEST_ID_GEN.incrementAndGet();

        MsgHeader header = new MsgHeader();
        header.setMagic(ProtocolConstants.MAGIC);
        header.setVersion(ProtocolConstants.VERSION);
        header.setRequestId(requestId);
        header.setSerialization((byte) SerializationTypeEnum.JSON.getType());
        header.setMsgType((byte) MsgType.REQUEST.getType());
        header.setStatus((byte) 0x1);

        MiniRpcRequest request = new MiniRpcRequest();
        request.setServiceVersion(this.serviceVersion);
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParams(args);

        MiniRpcProtocol<MiniRpcRequest> protocol = new MiniRpcProtocol<>();
        protocol.setHeader(header);
        protocol.setBody(request);

        String serviceKey = RpcServiceHelper.buildServiceKey(request.getClassName(), request.getServiceVersion());
        int invokerHashCode = (args != null && args.length > 0) ? RpcServiceHelper.hashCode(args[0].toString()).asInt() :
            RpcServiceHelper.hashCode(serviceKey).asInt();
        ServiceMeta serviceMetadata = registryService.discovery(serviceKey, invokerHashCode);
        if (serviceMetadata == null) {
            throw new RuntimeException(String.format("service not exist: %s", serviceKey));
        }

        String instanceKey = RpcServiceHelper.buildServiceInstanceKey(serviceMetadata);
        if (consumerMap.get(instanceKey) == null) {
            synchronized (consumerMap) {
                if (consumerMap.get(instanceKey) == null) {
                    RpcConsumer.newInstance(consumerMap, serviceMetadata, heartbeatInterval);
                }
            }
        }
        RpcConsumer rpcConsumer = consumerMap.get(instanceKey);
        if (rpcConsumer == null) {
            throw new RuntimeException(String.format("service connect failed: %s", serviceKey));
        }
        rpcConsumer.sendRequest(protocol);

        MiniRpcFuture<MiniRpcResponse> future = new MiniRpcFuture<>(new DefaultPromise<>(new DefaultEventLoop()), timeout);
        MiniRpcRequestHolder.REQUEST_MAP.put(requestId, future);
        // TODO hold request by ThreadLocal
        MiniRpcResponse rpcResponse = future.getPromise().get(future.getTimeout(), TimeUnit.MILLISECONDS);
        Type returnType = method.getGenericReturnType();
        RpcSerialization rpcSerialization = SerializationFactory.getRpcSerialization(header.getSerialization());
        return rpcResponse.getData() == null ? null : rpcSerialization.deserialize((String) rpcResponse.getData(), returnType);
    }
}
