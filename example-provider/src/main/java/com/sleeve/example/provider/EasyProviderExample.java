package com.sleeve.example.provider;

import com.sleeve.example.common.service.UserService;
import com.sleeve.example.provider.impl.UserServiceImpl;
import com.sleeve.rpc.bootstrap.ProviderBootstrap;
import com.sleeve.rpc.model.ServiceRegisterInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 简单的服务提供者示例
 */
public class EasyProviderExample {

    public static void main(String[] args) {
        // 要注册的服务
        List<ServiceRegisterInfo<?>> serviceRegisterInfoList = new ArrayList<>();
        ServiceRegisterInfo<UserService> serviceRegisterInfo = new ServiceRegisterInfo<>(UserService.class.getName(), UserServiceImpl.class);
        serviceRegisterInfoList.add(serviceRegisterInfo);

        // 服务提供者初始化
        ProviderBootstrap.init(serviceRegisterInfoList);
    }
}
