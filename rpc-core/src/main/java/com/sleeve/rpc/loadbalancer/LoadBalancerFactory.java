package com.sleeve.rpc.loadbalancer;

import com.sleeve.rpc.spi.SpiLoader;

/**
 * 负载均衡器工厂（工厂模式，用于获取负载均衡器对象）
 *
 */

public class LoadBalancerFactory {

    // 利用SPI机制，加载配置文件中的负载均衡相关的类
    static {
        SpiLoader.load(LoadBalancer.class);
    }


    /**
     * 默认负载均衡器
     */
    private static final LoadBalancer DEFAULT_LOAD_BALANCER = new RoundRobinLoadBalancer();

    /**
     * 获取实例
     *
     * @param key 负载均衡器的键名
     * @return 负载均衡器
     */
    public static LoadBalancer getInstance(String key) {
        return SpiLoader.getInstance(LoadBalancer.class, key);
    }
}
