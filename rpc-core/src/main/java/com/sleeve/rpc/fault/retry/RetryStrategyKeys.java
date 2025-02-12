package com.sleeve.rpc.fault.retry;

/**
 * 重试策略的键值
 */
public interface RetryStrategyKeys {

    String NO_RETRY = "noRetry";

    String FIXED_INTERVAL = "fixedInterval";

    String EXPONENTIAL_BACKOFF = "exponentialBackOff";
}
