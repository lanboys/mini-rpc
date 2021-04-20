package com.mini.rpc.registry;

import com.mini.rpc.common.RpcServiceHelper;
import com.mini.rpc.common.ServiceMeta;
import com.mini.rpc.provider.registry.RegistryFactory;
import com.mini.rpc.provider.registry.RegistryService;
import com.mini.rpc.provider.registry.RegistryType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RegistryTest {

    private RegistryService registryService;

    @Before
    public void init() throws Exception {
        registryService = RegistryFactory.getInstance("127.0.0.1:2181", RegistryType.ZOOKEEPER);
    }

    @After
    public void close() throws Exception {
        registryService.destroy();
    }

    @Test
    public void testAll() throws Exception {
        ServiceMeta serviceMeta1 = new ServiceMeta();
        serviceMeta1.setServiceAddr("127.0.0.1");
        serviceMeta1.setServicePort(8080);
        serviceMeta1.setServiceName("test1");
        serviceMeta1.setServiceVersion("1.0.0");

        ServiceMeta serviceMeta2 = new ServiceMeta();
        serviceMeta2.setServiceAddr("127.0.0.2");
        serviceMeta2.setServicePort(8080);
        serviceMeta2.setServiceName("test2");
        serviceMeta2.setServiceVersion("1.0.0");

        ServiceMeta serviceMeta3 = new ServiceMeta();
        serviceMeta3.setServiceAddr("127.0.0.3");
        serviceMeta3.setServicePort(8080);
        serviceMeta3.setServiceName("test3");
        serviceMeta3.setServiceVersion("1.0.0");

        registryService.register(serviceMeta1);
        registryService.register(serviceMeta2);
        registryService.register(serviceMeta3);

        ServiceMeta discovery1 = registryService.discovery("test1#1.0.0", "test1".hashCode());
        ServiceMeta discovery2 = registryService.discovery("test2#1.0.0", "test2".hashCode());
        ServiceMeta discovery3 = registryService.discovery("test3#1.0.0", "test3".hashCode());

        assert discovery1 != null;
        assert discovery2 != null;
        assert discovery3 != null;

        registryService.unRegister(discovery1);
        registryService.unRegister(discovery2);
        registryService.unRegister(discovery3);
    }

    @Test
    public void testAll1() throws Exception {
        ServiceMeta serviceMeta1 = new ServiceMeta();
        serviceMeta1.setServiceAddr("127.55.0.1");
        serviceMeta1.setServicePort(6080);
        serviceMeta1.setServiceName("test1");
        serviceMeta1.setServiceVersion("1.0.0");

        ServiceMeta serviceMeta2 = new ServiceMeta();
        serviceMeta2.setServiceAddr("192.168.0.2");
        serviceMeta2.setServicePort(8081);
        serviceMeta2.setServiceName("test1");
        serviceMeta2.setServiceVersion("1.0.0");

        ServiceMeta serviceMeta3 = new ServiceMeta();
        serviceMeta3.setServiceAddr("127.0.66.3");
        serviceMeta3.setServicePort(8080);
        serviceMeta3.setServiceName("test1");
        serviceMeta3.setServiceVersion("1.2.0");

        registryService.register(serviceMeta1);
        registryService.register(serviceMeta2);
        registryService.register(serviceMeta3);

        ServiceMeta discovery1 = registryService.discovery("test1#1.0.0", murmurHashCode("testX11"));
        ServiceMeta discovery2 = registryService.discovery("test1#1.0.0", murmurHashCode("testX22"));
        ServiceMeta discovery3 = registryService.discovery("test1#1.0.0", murmurHashCode("testY33"));
        ServiceMeta discovery4 = registryService.discovery("test1#1.0.0", murmurHashCode("testY44"));
        ServiceMeta discovery5 = registryService.discovery("test1#1.2.0", murmurHashCode("testX"));

        assert discovery1 != null;
        assert discovery2 != null;
        assert discovery3 != null;
        assert discovery4 != null;
        assert discovery5 != null;

        registryService.unRegister(discovery1);
        registryService.unRegister(discovery2);
        registryService.unRegister(discovery3);
        registryService.unRegister(discovery4);
        registryService.unRegister(discovery5);
    }

    private int murmurHashCode(String str) {
        int i = RpcServiceHelper.hashCode(str).asInt();
        System.out.println("murmurHashCode(): " + str + "  ->  " + i);
        return i;
    }
}
