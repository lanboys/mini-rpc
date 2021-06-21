package com.mini.rpc.consumer;

import com.mini.rpc.codec.MiniRpcDecoder;
import com.mini.rpc.codec.MiniRpcEncoder;
import com.mini.rpc.common.MiniRpcRequest;
import com.mini.rpc.common.RpcServiceHelper;
import com.mini.rpc.common.ServiceMeta;
import com.mini.rpc.handler.RpcHeartBeatHandler;
import com.mini.rpc.handler.RpcResponseHandler;
import com.mini.rpc.protocol.MiniRpcProtocol;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcConsumer {

    private ChannelFuture future;

    public static void newInstance(Map<String, RpcConsumer> consumerMap, ServiceMeta serviceMetadata, long heartbeatInterval) {
        new RpcConsumer(consumerMap, serviceMetadata, heartbeatInterval);
    }

    private RpcConsumer(Map<String, RpcConsumer> consumerMap, ServiceMeta serviceMetadata, long heartbeatInterval) {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4, new DefaultThreadFactory("consumer"));
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) {
                    socketChannel.pipeline()
                        .addLast(new RpcHeartBeatHandler(heartbeatInterval))
                        .addLast(new MiniRpcEncoder())
                        .addLast(new MiniRpcDecoder())
                        .addLast(new LoggingHandler())
                        .addLast(new RpcResponseHandler());
                }
            });

        connect(consumerMap, serviceMetadata, bootstrap, eventLoopGroup, true);
    }

    private void connect(Map<String, RpcConsumer> consumerMap, ServiceMeta serviceMetadata,
        Bootstrap bootstrap, EventLoopGroup eventLoopGroup, boolean localAddr) {
        // 是否为本地ip服务
        String serviceAddr = localAddr ? serviceMetadata.getLocalServiceAddr() : serviceMetadata.getServiceAddr();
        int servicePort = serviceMetadata.getServicePort();
        try {
            future = bootstrap.connect(serviceAddr, servicePort);
            future.addListener(future1 -> {
                if (future1.isSuccess()) {
                    log.info("connect rpc server {} on port {} success.", serviceAddr, servicePort);
                    String instanceKey = RpcServiceHelper.buildServiceInstanceKey(serviceMetadata);
                    consumerMap.put(instanceKey, RpcConsumer.this);
                    future.channel().closeFuture().addListener((ChannelFutureListener) closeFuture -> {
                        if (closeFuture.isSuccess()) {
                            eventLoopGroup.shutdownGracefully();
                            consumerMap.remove(instanceKey);
                            log.info("connect server {} on port {} close.", serviceAddr, servicePort);
                        }
                    });
                } else {
                    log.error("connect rpc server {} on port {} failed.", serviceAddr, servicePort);
                    future1.cause().printStackTrace();
                    if (localAddr) {
                        connect(consumerMap, serviceMetadata, bootstrap, eventLoopGroup, false);
                        return;
                    }
                    eventLoopGroup.shutdownGracefully();
                }
            });
            future.sync();
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            log.error("connect rpc server {} on port {} failed.", serviceAddr, servicePort, e);
        }
    }

    public void sendRequest(MiniRpcProtocol<MiniRpcRequest> protocol) {
        if (future != null) {
            future.channel().writeAndFlush(protocol);
        }
    }
}
