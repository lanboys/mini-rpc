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

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcConsumer {

    private ChannelFuture future;

    public static void newInstance(Map<String, RpcConsumer> consumerMap, ServiceMeta serviceMetadata) {
        new RpcConsumer(consumerMap, serviceMetadata);
    }

    private RpcConsumer(Map<String, RpcConsumer> consumerMap, ServiceMeta serviceMetadata) {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) {
                    socketChannel.pipeline()
                        .addLast(new RpcHeartBeatHandler())
                        .addLast(new MiniRpcEncoder())
                        .addLast(new MiniRpcDecoder())
                        .addLast(new RpcResponseHandler());
                }
            });

        try {
            future = bootstrap.connect(serviceMetadata.getServiceAddr(), serviceMetadata.getServicePort()).sync();
            if (!future.isSuccess()) {
                log.error("connect rpc server {} on port {} failed.", serviceMetadata.getServiceAddr(), serviceMetadata.getServicePort());
                future.cause().printStackTrace();
                eventLoopGroup.shutdownGracefully();
                return;
            }
            log.info("connect rpc server {} on port {} success.", serviceMetadata.getServiceAddr(), serviceMetadata.getServicePort());
            String instanceKey = RpcServiceHelper.buildServiceInstanceKey(serviceMetadata);
            consumerMap.put(instanceKey, this);
            future.channel().closeFuture().addListener((ChannelFutureListener) closeFuture -> {
                if (closeFuture.isSuccess()) {
                    eventLoopGroup.shutdownGracefully();
                    consumerMap.remove(instanceKey);
                    log.info("connect server {} on port {} close.", serviceMetadata.getServiceAddr(), serviceMetadata.getServicePort());
                }
            });
        } catch (InterruptedException e) {
            log.error("connect rpc server {} on port {} failed.", serviceMetadata.getServiceAddr(), serviceMetadata.getServicePort(), e);
        }
    }

    public void sendRequest(MiniRpcProtocol<MiniRpcRequest> protocol) {
        if (future != null) {
            future.channel().writeAndFlush(protocol);
        }
    }
}
