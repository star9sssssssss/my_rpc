package com.sleeve.core;


import com.sleeve.core.config.RegistryConfig;
import com.sleeve.core.config.RpcConfig;
import com.sleeve.core.constant.RpcConstant;
import com.sleeve.core.registry.Registry;
import com.sleeve.core.registry.RegistryFactory;
import com.sleeve.core.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcApplication {
    private static volatile RpcConfig rpcConfig;


    /**
     * 框架初始化，支持自定义配置
     * @param newRpcConfig
     */
    public static void init(RpcConfig newRpcConfig) {
        // rpc基础配置初始化 => RpcConfig
        rpcConfig = newRpcConfig;
        log.info("rpc init, config = {}", newRpcConfig.toString());
        // 注册中心初始化 => RegistryConfig
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistryType());
        registry.init(registryConfig);
        log.info("registry init, config = {}", registryConfig.toString());

        // 创建并注册 shutdown hook， JVM退出时执行操作 (下线当前节点的所有服务)
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }

    /**
     * 读取配置文件的方式
     */
    public static void init() {
        RpcConfig newRpcConfig;
        try {
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            // 配置加载失败，使用默认配置
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }


    /**
     * 获得rpc配置属性, 使用单例模式
     * @return
     */
    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }


}
