package com.sleeve.core.registry;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
/**
 * 本地的注册中心
 */
public class LocalRegistry {

    /**
     * 注册信息存储
     */
    private static final Map<String, Class<?>> map = new ConcurrentHashMap<String, Class<?>>();


    /**
     * 注册服务
     * @param serviceName 服务名称
     * @param implClass 服务的实现类
     */
    public static void register(String serviceName, Class<?> implClass) {
        map.put(serviceName, implClass);
    }

    /**
     * 获取服务
     * @param serviceName 服务名称
     * @return 服务的实现类
     */
    public static Class<?> get(String serviceName) {
        return map.get(serviceName);
    }

    /**
     * 删除服务
     * @param serviceName 服务名称
     */
    public static void remove(String serviceName) {
        map.remove(serviceName);
    }
}
