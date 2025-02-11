package com.sleeve.rpc.loadbalancer;

/**
 * 负载均衡器键名
 */
public interface LoadBalancerKeys {

    String ROUND_ROBIN = "roundRobin";

    String RANDOM = "random";

    String CONSISTENT_HASH = "consistentHash";
}
