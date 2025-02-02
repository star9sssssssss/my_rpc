package com.sleeve.core.registry;

import com.sleeve.core.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注册中心的服务缓存 (支持多个服务)
 */
public class RegistryServiceMultiCache {

    /**
     * 服务缓存
     */
    Map<String, List<ServiceMetaInfo>> serviceCache = new ConcurrentHashMap<>();

    /**
     * 对缓存进行写操作
     *
     * @param serviceKey 服务键名
     * @param newServiceCache 更新后的缓存列表
     * @return
     */
    void writeCache(String serviceKey, List<ServiceMetaInfo> newServiceCache) {
        this.serviceCache.put(serviceKey, newServiceCache);
    }

    /**
     * 对缓存进行读操作
     * @param serviceKey
     * @return
     */
    List<ServiceMetaInfo> readCache(String serviceKey) {
        return this.serviceCache.get(serviceKey);
    }

    /**
     * 对缓存进行清空操作
     */
    void clearCache(String serviceKey) {
        this.serviceCache.remove(serviceKey);
    }

}
