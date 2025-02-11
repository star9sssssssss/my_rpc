package com.sleeve.rpc.proxy;

import com.sleeve.rpc.RpcApplication;

import java.lang.reflect.Proxy;

/**
 * 服务代理工厂 (用于创建代理对象)
 */
public class ServiceProxyFactory {


    /**
     * 根据服务类获取代理对象
     * @param serviceClass
     * @return
     * @param <T>
     */
    public static <T> T getProxy(Class<T> serviceClass) {
        if (RpcApplication.getRpcConfig().isMock()) { // 是模拟对象
            return getMockProxy(serviceClass);
        }
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(),
                        new Class[]{serviceClass},
                        new ServiceProxy());
    }

    /**
     * 根据服务类型获得mock代理
     * 对象
     * @param serviceClass
     * @return
     * @param <T>
     */
    public static <T> T getMockProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new MockServiceProxy());
    }

}
