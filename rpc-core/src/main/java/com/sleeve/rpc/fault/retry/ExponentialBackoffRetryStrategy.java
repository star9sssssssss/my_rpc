package com.sleeve.rpc.fault.retry;

import com.github.rholder.retry.*;
import com.sleeve.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;


/**
 * 实现指数退避算法
 *
 */
@Slf4j
public class ExponentialBackoffRetryStrategy implements RetryStrategy {

    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        log.info("本次执行的重试策略为: {}", ExponentialBackoffRetryStrategy.class.getSimpleName());
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse> newBuilder()
                .retryIfExceptionOfType(Exception.class)
                .withWaitStrategy(WaitStrategies.exponentialWait(2L, 10L, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(10))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        if (attempt.hasException()) {
                            log.info("重试次数 {}", attempt.getAttemptNumber());
                        }
                    }
                }).build();
        return retryer.call(callable);
    }

}
