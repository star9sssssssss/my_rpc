package com.sleeve.example.provider;

import com.sleeve.example.common.service.UserService;
import com.sleeve.rpc.registry.LocalRegistry;
import com.sleeve.rpc.server.VertxHttpServer;

/**
 * 简单的服务提供者示例
 */
public class EasyProviderExample {
    public static void main(String[] args) {
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
        // 启动web服务
        VertxHttpServer server = new VertxHttpServer();
        server.doStart(8080);
    }
}
