package com.sleeve.rpc.fault.retry;

import com.sleeve.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * 不进行任何重试操作 ---> 重试策略
 */
@Slf4j
public class NoRetryStrategy implements RetryStrategy{

    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        log.info("调用的重试策略是: {}", NoRetryStrategy.class.getSimpleName());
        return callable.call();
    }
}
