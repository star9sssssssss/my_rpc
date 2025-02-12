package com.sleeve.rpc.fault.tolerant;

import com.sleeve.rpc.model.RpcResponse;
import com.sleeve.rpc.model.ServiceMetaInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 转移到其他服务节点 - 容错策略
 */
@Slf4j
public class FailOverTolerantStrategy implements TolerantStrategy {

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.info("本次容错处理策略是转移其他节点处理: {}", FailBackTolerantStrategy.class.getSimpleName(), e);
        RpcResponse rpcResponse = new RpcResponse();
        // todo 可自行扩展，获取其他服务节点并调用
        if (!context.containsKey("AllServices")) {
            return rpcResponse;
        }
        if (!context.containsKey("CurrentService")) {
            return rpcResponse;
        }
        List<ServiceMetaInfo> serviceMetaInfoList = (List<ServiceMetaInfo>) context.get("AllServices");
        ServiceMetaInfo service = (ServiceMetaInfo) context.get("CurrentService");
        serviceMetaInfoList.stream().filter(selectService -> selectService != service).forEach(s -> {
            System.out.println("本次选择的节点信息是 " + s.getServiceNodeKey());
        });
        return null;
    }

}
