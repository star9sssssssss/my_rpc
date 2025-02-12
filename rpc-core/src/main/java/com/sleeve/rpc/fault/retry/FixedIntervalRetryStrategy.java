package com.sleeve.rpc.fault.retry;

import com.github.rholder.retry.*;
import com.sleeve.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


/**
 * 固定间隔的重试策略
 */
@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy{

    /**
     * 固定间隔重试实现
     * 1. 重试条件：使用 retryIfExceptionOfType 方法指定当出现 Exception 异常时重试。
     * 2. 重试等待策略：使用 withWaitStrategy 方法指定策略，选择 fixedWait 固定时间间隔策略。
     * 3. 重试停止策略：使用 withStopStrategy 方法指定策略，选择 stopAfterAttempt 超过最大重试次数停止。
     * 4. 重试工作：使用 withRetryListener 监听重试，每次重试时，除了再次执行任务外，还能够打印当前的重试次数。
     * @param callable
     * @return
     * @throws ExecutionException
     * @throws RetryException
     */
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws ExecutionException, RetryException {
        log.info("本次执行的重试策略为: {}", FixedIntervalRetryStrategy.class.getSimpleName());
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse> newBuilder()
                .retryIfException() // 出现异常重试
                .withWaitStrategy(WaitStrategies.fixedWait(3L, TimeUnit.SECONDS)) // 重试时间固定3s
                .withStopStrategy(StopStrategies.stopAfterAttempt(3)) // 最多三次停止重试
                .withRetryListener(new RetryListener() { // 指定每次重试时，执行的内容
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        if (attempt.hasException()) {
                            log.info("重试次数 {}", attempt.getAttemptNumber());
                        }
                    }
                })
                .build();
        return retryer.call(callable);
    }
}
