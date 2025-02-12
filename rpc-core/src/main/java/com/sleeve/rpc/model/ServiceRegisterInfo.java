package com.sleeve.rpc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务信息注册类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceRegisterInfo<T> {

    // 服务名称
    private String serviceName;

    // 服务对应的实现类
    private Class<? extends T> implClass;
}
