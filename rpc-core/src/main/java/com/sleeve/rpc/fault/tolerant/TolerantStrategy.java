package com.sleeve.rpc.fault.tolerant;

import com.sleeve.rpc.model.RpcResponse;

import java.util.Map;

/**
 * 容错策略常用方法
 */
public interface TolerantStrategy {

    /**
     * 处理容错
     * @param context 上下文，用于处理数据
     * @param e 异常
     * @return
     */
    RpcResponse doTolerant(Map<String, Object> context, Exception e);
}
