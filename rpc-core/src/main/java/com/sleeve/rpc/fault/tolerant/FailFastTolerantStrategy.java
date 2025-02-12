package com.sleeve.rpc.fault.tolerant;

import com.sleeve.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 快速失败 - 容错策略（立刻通知外层调用方）
 */
@Slf4j
public class FailFastTolerantStrategy implements TolerantStrategy {

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.info("本次容错处理策略是快速处理: {}", FailFastTolerantStrategy.class.getSimpleName(), e);
        throw new RuntimeException("服务报错", e);
    }
}
