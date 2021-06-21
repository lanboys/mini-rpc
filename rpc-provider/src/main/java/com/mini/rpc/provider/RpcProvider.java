package com.mini.rpc.provider;

import com.mini.rpc.codec.MiniRpcDecoder;
import com.mini.rpc.codec.MiniRpcEncoder;
import com.mini.rpc.common.RpcServiceHelper;
import com.mini.rpc.common.ServiceMeta;
import com.mini.rpc.common.utils.IPUtil;
import com.mini.rpc.handler.RpcIdleStateHandler;
import com.mini.rpc.handler.RpcRequestHandler;
import com.mini.rpc.provider.annotation.RpcService;
import com.mini.rpc.provider.registry.RegistryService;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcProvider implements InitializingBean, BeanPostProcessor {

    private String localServiceAddr;
    private String publicServiceAddress;
    private final int serverPort;
    private final long readerIdleTime;
    private final RegistryService serviceRegistry;

    private final Map<String, ServiceMeta> rpcServiceMap = new HashMap<>();

    public RpcProvider(int serverPort, RegistryService serviceRegistry, long readerIdleTime) {
        this.serverPort = serverPort;
        this.serviceRegistry = serviceRegistry;
        this.readerIdleTime = readerIdleTime;
        try {
            this.localServiceAddr = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("fail to get local address.", e);
        }
        this.publicServiceAddress = IPUtil.getSelfPublicIp();
        if (publicServiceAddress == null) {
            throw new RuntimeException("fail to get public address");
        }
    }

    @Override
    public void afterPropertiesSet() {
        new Thread(() -> {
            try {
                startRpcServer();
            } catch (Exception e) {
                log.error("start rpc server error.", e);
            }
        }).start();
    }

    private void startRpcServer() throws Exception {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new RpcIdleStateHandler(readerIdleTime))
                                    .addLast(new MiniRpcEncoder())
                                    .addLast(new MiniRpcDecoder())
                                    .addLast(new LoggingHandler())
                                    .addLast(new RpcRequestHandler(rpcServiceMap));
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture channelFuture = bootstrap.bind(this.localServiceAddr, this.serverPort).sync();
            log.info("server addr {} started on port {}", this.localServiceAddr, this.serverPort);
            channelFuture.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        RpcService rpcService = AnnotationUtils.findAnnotation(bean.getClass(), RpcService.class);
        if (rpcService == null) {
            return bean;
        }
        String serviceName = rpcService.serviceInterface().getName();
        Class<?> aClass;
        try {
            aClass = Class.forName(serviceName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        String serviceVersion = rpcService.serviceVersion();
        try {
            ServiceMeta serviceMeta = new ServiceMeta();
            serviceMeta.setLocalServiceAddr(localServiceAddr);
            serviceMeta.setServiceAddr(publicServiceAddress);
            serviceMeta.setServicePort(serverPort);
            serviceMeta.setServiceName(serviceName);
            serviceMeta.setServiceVersion(serviceVersion);
            serviceMeta.setActualService(bean);
            serviceMeta.setServiceClass(aClass);

            serviceRegistry.register(serviceMeta);
            rpcServiceMap.put(RpcServiceHelper.buildServiceKey(serviceMeta.getServiceName(), serviceMeta.getServiceVersion()), serviceMeta);
        } catch (Exception e) {
            log.error("failed to register service {}#{}", serviceName, serviceVersion, e);
        }
        return bean;
    }
}
