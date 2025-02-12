package com.sleeve.rpc.fault.tolerant;

import com.sleeve.rpc.spi.SpiLoader;

/**
 * 容错策略工厂，通过SPI机制快速获得相应的实例
 */
public class TolerantStrategyFactory {

    static {
        SpiLoader.load(TolerantStrategy.class);
    }

    // 默认的容错处理机制
    public static final TolerantStrategy DEFAULT_TOLERANT_STRATEGY = new FailFastTolerantStrategy();

    /**
     * 根据key获取容错策略实例
     * @param key 指定不同的容错策略
     * @return
     */
    public static TolerantStrategy getInstance(String key) {
        return SpiLoader.getInstance(TolerantStrategy.class, key);
    }
}
