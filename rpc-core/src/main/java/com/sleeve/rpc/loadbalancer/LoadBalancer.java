package com.sleeve.rpc.loadbalancer;

import com.sleeve.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

/**
 * 负载均衡实现器
 */
public interface LoadBalancer {

    /**
     * 选择服务调用
     * @param requestParams 请求参数
     * @param serviceMetaInfoList 所有的服务信息
     * @return
     */
    ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList);
}
