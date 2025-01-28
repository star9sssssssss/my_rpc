package com.sleeve.core.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.sleeve.rpc.model.RpcRequest;
import com.sleeve.rpc.model.RpcResponse;
import com.sleeve.rpc.serializer.JdkSerializer;
import com.sleeve.rpc.serializer.Serializer;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 实现动态代理 (JDK 动态代理)
 */
public class ServiceProxy implements InvocationHandler {

    /**
     *
     * @param proxy the proxy instance that the method was invoked on
     * @param method 当前调用的方法
     * @param args 当前调用方法的参数
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Serializer serializer = new JdkSerializer();

        // 构建发送请求类
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName()) // 定义该方法的类
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        try {
            byte[] bytes = serializer.serialize(rpcRequest);
            byte[] result;
            // TODO 这里的地址是定量，应该采用注册中心和服务发现机制解决
            try (HttpResponse httpResponse = HttpRequest
                    .post("http://localhost:8080")
                    .body(bytes).execute()) {
                result = httpResponse.bodyBytes();
            }
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            System.out.println("本次调用的信息 " + rpcResponse.getMessage());
            System.out.println("返回参数的类型" + rpcResponse.getDataType());
            return rpcResponse.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
