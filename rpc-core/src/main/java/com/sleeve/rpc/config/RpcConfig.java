package com.sleeve.rpc.config;

import com.sleeve.rpc.loadbalancer.LoadBalancerKeys;
import com.sleeve.rpc.serializer.SerializerKeys;
import lombok.Data;

/**
 * RPC 框架配置
 */
@Data
public class RpcConfig {

    /**
     * 名称
     */
    private String name = "my-rpc";

    /**
     * 版本号
     */
    private String version = "1.0";

    /**
     * 服务器主机名
     */
    private String serverHost = "localhost";

    /**
     * 服务器端口号
     */
    private Integer serverPort = 8080;

    /**
     * 是否开启模拟调用
     */
    private Boolean mock = false;

    /**
     * 序列化器
     * @return 类型
     */
    private String serializer = SerializerKeys.JDK;

    /**
     * 注册中心配置
     * @return
     */
    private RegistryConfig registryConfig = new RegistryConfig();

    /**
     * 负载均衡器
     */
    private String loadBalancer = LoadBalancerKeys.ROUND_ROBIN;

    public Boolean isMock() {
        return mock;
    }



}
