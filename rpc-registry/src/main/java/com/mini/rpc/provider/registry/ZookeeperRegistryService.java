package com.mini.rpc.provider.registry;

import com.mini.rpc.common.RpcServiceHelper;
import com.mini.rpc.common.ServiceMeta;
import com.mini.rpc.provider.registry.loadbalancer.ZKConsistentHashLoadBalancer;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;

import java.io.IOException;
import java.util.List;

public class ZookeeperRegistryService implements RegistryService {

    public static final int BASE_SLEEP_TIME_MS = 1000;
    public static final int MAX_RETRIES = 3;
    public static final String ZK_BASE_PATH = "/mini-rpc";

    private final ServiceDiscovery<ServiceMeta> serviceDiscovery;
    private final ZKConsistentHashLoadBalancer loadBalancer;

    public ZookeeperRegistryService(String registryAddr) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.newClient(registryAddr,
                new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES));
        client.start();
        this.serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMeta.class)
                .client(client)
                .basePath(ZK_BASE_PATH)
                .build();
        this.serviceDiscovery.start();
        loadBalancer = new ZKConsistentHashLoadBalancer();
    }

    @Override
    public void register(ServiceMeta serviceMeta) throws Exception {
        ServiceInstance<ServiceMeta> serviceInstance = getServiceMetaServiceInstance(serviceMeta);
        serviceDiscovery.registerService(serviceInstance);
    }

    @Override
    public void unRegister(ServiceMeta serviceMeta) throws Exception {
        ServiceInstance<ServiceMeta> serviceInstance = getServiceMetaServiceInstance(serviceMeta);
        serviceDiscovery.unregisterService(serviceInstance);
    }

    private ServiceInstance<ServiceMeta> getServiceMetaServiceInstance(ServiceMeta serviceMeta) throws Exception {
        return ServiceInstance.<ServiceMeta>builder()
                .name(RpcServiceHelper.buildServiceKey(serviceMeta.getServiceName(),
                        serviceMeta.getServiceVersion()))
                .address(serviceMeta.getServiceAddr())
                .port(serviceMeta.getServicePort())
                .payload(serviceMeta)
                .build();
    }

    @Override
    public ServiceMeta discovery(String serviceName, int invokerHashCode) throws Exception {
        List<ServiceInstance<ServiceMeta>> serviceInstances = (List<ServiceInstance<ServiceMeta>>)
                serviceDiscovery.queryForInstances(serviceName);
        ServiceInstance<ServiceMeta> instance = loadBalancer.select(serviceInstances, invokerHashCode);
        return instance != null ? instance.getPayload() : null;
    }

    @Override
    public void destroy() throws IOException {
        serviceDiscovery.close();
    }
}
