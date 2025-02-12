package com.sleeve.rpc.proxy;

import cn.hutool.core.collection.CollUtil;
import com.sleeve.rpc.RpcApplication;
import com.sleeve.rpc.config.RpcConfig;
import com.sleeve.rpc.constant.RpcConstant;
import com.sleeve.rpc.fault.retry.RetryStrategy;
import com.sleeve.rpc.fault.retry.RetryStrategyFactory;
import com.sleeve.rpc.fault.tolerant.TolerantStrategy;
import com.sleeve.rpc.fault.tolerant.TolerantStrategyFactory;
import com.sleeve.rpc.loadbalancer.LoadBalancer;
import com.sleeve.rpc.loadbalancer.LoadBalancerFactory;
import com.sleeve.rpc.model.RpcRequest;
import com.sleeve.rpc.model.RpcResponse;
import com.sleeve.rpc.model.ServiceMetaInfo;
import com.sleeve.rpc.registry.Registry;
import com.sleeve.rpc.registry.RegistryFactory;
import com.sleeve.rpc.serializer.Serializer;
import com.sleeve.rpc.serializer.SerializerFactory;
import com.sleeve.rpc.server.tcp.VertxTcpClient;


import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实现动态代理 (JDK 动态代理)
 */
public class ServiceProxy implements InvocationHandler {

    /**
     *
     * @param proxy the proxy instance that the method was invoked on
     * @param method 当前调用的方法
     * @param args 当前调用方法的参数
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

    // 构建发送请求类
    RpcRequest rpcRequest = RpcRequest.builder()
            .serviceName(method.getDeclaringClass().getName()) // 定义该方法的类
            .methodName(method.getName())
            .parameterTypes(method.getParameterTypes())
            .args(args)
            .build();

        // 使用注册中心获取服务对象
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistryType());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(method.getDeclaringClass().getName());
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);

        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        if (CollUtil.isEmpty(serviceMetaInfoList)) {
            throw new RuntimeException("暂无服务地址");
        }

        // 使用负载均衡选择需要服务的对象 TODO 选择的服务器无法使用，该如何调整
        LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
        // 将调用方法名（请求路径）作为负载均衡参数
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("methodName", rpcRequest.getMethodName());
        ServiceMetaInfo selectMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);

        // 暂时取第一个作为服务对象
        //ServiceMetaInfo selectMetaInfo = serviceMetaInfoList.get(0);

        // 发送请求 (HTTP)
//            try (HttpResponse httpResponse = HttpRequest
//                    .post(selectMetaInfo.getServiceAddress())
//                    .body(bytes).execute()) {
//                result = httpResponse.bodyBytes();
//            }
//            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);

        // 发送请求 (TCP)
        // 发送 TCP 请求

        RpcResponse rpcResponse;
        // 使用重试机制
        try {
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
            rpcResponse = retryStrategy.doRetry(() -> {
                return VertxTcpClient.doRequest(rpcRequest, selectMetaInfo);
            });
        } catch (Exception e) {
            // 容错机制
            TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
            // 当使用Fail_Over机制时，需要选择其他的节点
            HashMap<String, Object> map = new HashMap<>();
            map.put("AllServices", serviceMetaInfoList);
            map.put("CurrentService", selectMetaInfo);
            rpcResponse = tolerantStrategy.doTolerant(map, e);
        }
        System.out.println("本次调用的信息 " + rpcResponse.getMessage());
        System.out.println("返回参数的类型" + rpcResponse.getDataType());
        return rpcResponse.getData();
    }
}
