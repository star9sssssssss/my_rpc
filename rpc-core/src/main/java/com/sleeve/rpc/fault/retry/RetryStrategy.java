package com.sleeve.rpc.fault.retry;


import com.sleeve.rpc.model.RpcRequest;
import com.sleeve.rpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 重试策略
 */
public interface RetryStrategy {


    /**
     * 实现重试
     * @param callable
     * @return
     * @throws Exception
     */
    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;
}
