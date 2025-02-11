package com.sleeve.example.common.test;

import com.sleeve.rpc.loadbalancer.LoadBalancer;
import com.sleeve.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

public class TestLoadBalancer implements LoadBalancer {
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        return serviceMetaInfoList.get(0);
    }
}
