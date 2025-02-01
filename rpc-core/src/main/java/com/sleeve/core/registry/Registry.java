package com.sleeve.core.registry;


import com.sleeve.core.config.RegistryConfig;
import com.sleeve.core.model.ServiceMetaInfo;

import java.util.List;

/**
 * 注册中心
 */
public interface Registry {

    // 初始化
    void init(RegistryConfig registryConfig);

    // 注册服务 (服务端)
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    // 注销服务
    void unRegister(ServiceMetaInfo serviceMetaInfo);

    // 服务发现，获取某服务的所有节点 (消费端)
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    // 服务消耗
    void destroy();
}
