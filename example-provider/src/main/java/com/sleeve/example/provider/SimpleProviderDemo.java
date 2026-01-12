package com.sleeve.example.provider;

import com.sleeve.easyrpc.server.vertx.VertxHttpServer;
import com.sleeve.example.common.service.CommonService;
import com.sleeve.example.common.service.UserService;
import com.sleeve.easyrpc.registry.LocalRegistry;
import com.sleeve.example.provider.impl.CommonServiceImpl;
import com.sleeve.example.provider.impl.UserServiceImpl;

public class SimpleProviderDemo {

    public static void main(String[] args) {
        LocalRegistry.register(CommonService.class.getName(), CommonServiceImpl.class);
        // 启动web服务
        VertxHttpServer vertxHttpServer = new VertxHttpServer();
        vertxHttpServer.doStart(8888);
    }

}
