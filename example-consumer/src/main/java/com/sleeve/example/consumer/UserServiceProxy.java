package com.sleeve.example.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.sleeve.rpc.model.RpcRequest;
import com.sleeve.rpc.model.RpcResponse;
import com.sleeve.rpc.serializer.JdkSerializer;
import com.sleeve.rpc.serializer.Serializer;
import com.sleeve.example.common.model.User;
import com.sleeve.example.common.service.UserService;


import java.io.IOException;

/**
 * 静态代理
 *
 */
public class UserServiceProxy implements UserService {

    @Override
    public User getUser(User user) {
        final Serializer serializer = new JdkSerializer();

        // 构建发送请求类
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[]{User.class})
                .args(new Object[]{user})
                .build();

        try {
            byte[] bytes = serializer.serialize(rpcRequest);
            byte[] result;
            try (HttpResponse httpResponse = HttpRequest
                    .post("http://localhost:8080")
                    .body(bytes).execute()) {
                result = httpResponse.bodyBytes();
            }
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return (User) rpcResponse.getData();
        } catch (IOException e) {
           e.printStackTrace();
        }
        return null;
    }
}
