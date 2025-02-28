package com.sleeve.example.provider;

import com.sleeve.rpc.RpcApplication;
import com.sleeve.rpc.config.RegistryConfig;
import com.sleeve.rpc.config.RpcConfig;
import com.sleeve.rpc.model.ServiceMetaInfo;
import com.sleeve.rpc.registry.LocalRegistry;
import com.sleeve.rpc.registry.Registry;
import com.sleeve.rpc.registry.RegistryFactory;
import com.sleeve.rpc.server.tcp.VertxTcpServer;
import com.sleeve.example.common.service.UserService;

// 使用注册中心的rpc
public class ProviderExampleByRegistry {

    public static void main(String[] args) {
        RpcApplication.init();

        // 注册服务
        String name = UserService.class.getName();
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
        System.out.println(LocalRegistry.get(name));

        // 注册到服务中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();

        // 根据key获得相应的注册中心 如 etcd => EtcdRegistry
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistryType());
        ServiceMetaInfo metaInfo = new ServiceMetaInfo();
        metaInfo.setServiceName(name);
        metaInfo.setServiceHost(rpcConfig.getServerHost());
        metaInfo.setServicePort(rpcConfig.getServerPort());

//        ServiceMetaInfo test1 = new ServiceMetaInfo();
//        test1.setServiceName(name);
//        test1.setServiceHost("192.168.177.130");
//        test1.setServicePort(8081);
//
//        ServiceMetaInfo test2 = new ServiceMetaInfo();
//        test2.setServiceName(name);
//        test2.setServiceHost("192.168.177.131");
//        test2.setServicePort(8082);

        try {
            registry.register(metaInfo);
//            registry.register(test1);
//            registry.register(test2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 使用HTTP
//        VertxHttpServer server = new VertxHttpServer();
//        server.doStart(RpcApplication.getRpcConfig().getServerPort());

        // 使用TCP
        VertxTcpServer tcpServer = new VertxTcpServer();
        tcpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
