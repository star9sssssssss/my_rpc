package com.sleeve.example.provider;

import com.sleeve.core.RpcApplication;
import com.sleeve.example.common.service.UserService;
import com.sleeve.core.registry.LocalRegistry;
import com.sleeve.core.server.VertxHttpServer;

/**
 * 简单的服务提供者示例
 */
public class EasyProviderExample {

    public static void main(String[] args) {
        // 初始化全局配置
        RpcApplication.init();
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
        VertxHttpServer server = new VertxHttpServer();
        server.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
