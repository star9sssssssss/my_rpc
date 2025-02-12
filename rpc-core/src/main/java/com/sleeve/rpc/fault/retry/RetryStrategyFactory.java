package com.sleeve.rpc.fault.retry;

import com.sleeve.rpc.spi.SpiLoader;

/**
 * 快速获得重试机制实例的工厂类
 */
public class RetryStrategyFactory {

    static {
        SpiLoader.load(RetryStrategy.class);
    }


    // 默认使用固定间隔重试机制
    public static final RetryStrategy DEFAULT_RETRY_STRATEGY = new FixedIntervalRetryStrategy();

    public static RetryStrategy getInstance(String key) {
        return SpiLoader.getInstance(RetryStrategy.class, key);
    }
}
