package com.sleeve.core.registry;

import com.sleeve.core.spi.SpiLoader;

/**
 * 使用工厂模式 获取注册中心对象
 */
public class RegistryFactory {

    static {
        SpiLoader.load(Registry.class);
    }

    // 默认的注册中心
    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();

    // 获取实例
    public static Registry getInstance(String key) {
        return SpiLoader.getInstance(Registry.class, key);
    }
}

