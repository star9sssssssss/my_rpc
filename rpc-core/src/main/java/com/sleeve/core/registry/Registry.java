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


    /**
     * 服务发现，获取某服务的所有节点 (消费端)
     * @param serviceKey 如 userService:1.0
     * @return
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    // 注册中心注销
    void destroy();

    // 注册中心的心跳检测服务 (服务端)
    void heartBeat();

    /**
     * 监听 (消费端)
     * @param serviceNodeKey 监听的服务名称
     */
    void watch(String serviceNodeKey);
}
