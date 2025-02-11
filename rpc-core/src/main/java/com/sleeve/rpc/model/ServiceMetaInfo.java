package com.sleeve.rpc.model;


import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * 服务元信息 (注册信息)
 *
 */
@Data
public class ServiceMetaInfo {


    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务版本号
     */
    private String serviceVersion = "1.0";

    /**
     * 服务域名
     */
    private String serviceHost;

    /**
     * 服务端口号
     */
    private Integer servicePort;

    /**
     * 服务分组（暂未实现）
     */
    private String serviceGroup = "default";


    /**
     * 获取服务的键名, 由服务名称和服务版本组成
     * @return
     */
    public String getServiceKey() {
        // 后续可拓展分组 return String.format("%s:%s", serviceName, serviceVersion, serviceGroup);
        // userService:1.0
        return String.format("%s:%s", serviceName, serviceVersion);
    }


    /**
     * 获取服务注册节点键名
     *
     * @return
     */
    public String getServiceNodeKey() {
        // userService:1.0/localhost:8080
        return String.format("%s/%s:%s", getServiceKey(), serviceHost, servicePort);
    }


    /**
     * 获取完整服务地址
     *
     * @return
     */
    public String getServiceAddress() {
        if (!StrUtil.contains(serviceHost, "http")) {
            return String.format("http://%s:%s", serviceHost, servicePort);
        }
        return String.format("%s:%s", serviceHost, servicePort);
    }

}
