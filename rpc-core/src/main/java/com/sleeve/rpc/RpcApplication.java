package com.sleeve.rpc;


import com.sleeve.rpc.config.RegistryConfig;
import com.sleeve.rpc.config.RpcConfig;
import com.sleeve.rpc.constant.RpcConstant;
import com.sleeve.rpc.registry.Registry;
import com.sleeve.rpc.registry.RegistryFactory;
import com.sleeve.rpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcApplication {
    private static volatile RpcConfig rpcConfig;


    /**
     * 框架初始化，支持自定义配置
     * TODO 暂时未区别客户端和服务端
     * @param newRpcConfig
     */
    public static void init(RpcConfig newRpcConfig) {
        // rpc基础配置初始化 => RpcConfig
        rpcConfig = newRpcConfig;
        log.info("rpc init, config = {}", newRpcConfig.toString());
        // 注册中心初始化 => RegistryConfig
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistryType());
        // 开启心跳检测
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
                    log.info("单例模式获取RpcConfig初始化");
                    init();
                }
            }
        }
        log.info("已通过单例模式获取RpcConfig");
        return rpcConfig;
    }


}
