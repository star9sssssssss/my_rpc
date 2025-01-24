package com.sleeve.rpc.server;


import com.sleeve.rpc.model.RpcRequest;
import com.sleeve.rpc.model.RpcResponse;
import com.sleeve.rpc.registry.LocalRegistry;
import com.sleeve.rpc.serializer.JdkSerializer;
import com.sleeve.rpc.serializer.Serializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * http 请求处理器
 */
public class HttpServerHandler implements Handler<HttpServerRequest> {


    /**
     * 1.反序列化请求对象，并获取参数
     * 2.根据服务名称从本地注册器中取到对应的服务实现类
     * 3.通过反射机制调用方法，得到返回结果
     * 4.对返回的结果进行封装和序列化，并写到响应中
     * @param request
     */
    @Override
    public void handle(HttpServerRequest request) {
        // 指定序列化器
        final Serializer serializer = new JdkSerializer();

        // 记录日志
        System.out.println("Received request: " + request.method() + " " + request.uri());

        // 异步处理请求
        request.bodyHandler(body -> {
            // 1.反序列化请求对象，并获取参数
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (IOException e) {
               e.printStackTrace();
            }
            // 构造响应结果对象
            RpcResponse rpcResponse = new RpcResponse();
            // 请求为null，直接返回
            if (rpcRequest == null) {
                rpcResponse.setMessage("rpcRequest is null");
                doResponse(request, rpcResponse, serializer);
                return;
            }

            // 通过反射调用实现服务类
            // 2.根据服务名称从本地注册器中取到对应的服务实现类
            Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
            try {
                // 3.通过反射机制调用方法，得到返回结果
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                // 封装返回结果
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("OK");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
                throw new RuntimeException(e);
            }
            // 4.对返回的结果进行封装和序列化，并写到响应中
            doResponse(request, rpcResponse, serializer);
        });
    }

    /**
     * 对本次请求的响应封装为RpcResponse并序列化返回
     * @param request http请求
     * @param rpcResponse rpc的响应，
     * @param serializer 序列化器, 对返回的数据进行序列化
     */
    void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse httpServerResponse = request.response().putHeader("content-type", "application/json");
        try {
            byte[] bytes = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(bytes));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}
