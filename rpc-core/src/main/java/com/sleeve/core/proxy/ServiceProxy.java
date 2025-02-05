package com.sleeve.core.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.sleeve.core.RpcApplication;
import com.sleeve.core.config.RpcConfig;
import com.sleeve.core.constant.RpcConstant;
import com.sleeve.core.model.RpcRequest;
import com.sleeve.core.model.RpcResponse;
import com.sleeve.core.model.ServiceMetaInfo;
import com.sleeve.core.protocol.*;
import com.sleeve.core.registry.Registry;
import com.sleeve.core.registry.RegistryFactory;
import com.sleeve.core.serializer.JdkSerializer;
import com.sleeve.core.serializer.Serializer;
import com.sleeve.core.serializer.SerializerFactory;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;


import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        // 构建发送请求类
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName()) // 定义该方法的类
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        try {
            byte[] bytes = serializer.serialize(rpcRequest);
            //byte[] result;

            // 使用注册中心获取服务对象
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistryType());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(method.getDeclaringClass().getName());
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);

            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                throw new RuntimeException("暂无服务地址");
            }

            // TODO 暂时找localhost
            ServiceMetaInfo selectMetaInfo = null;
            for (ServiceMetaInfo metaInfo : serviceMetaInfoList) {
                if ("localhost".equals(metaInfo.getServiceHost())) {
                    selectMetaInfo = metaInfo;
                    break;
                }
            }

            // 暂时取第一个作为服务对象
            //ServiceMetaInfo selectMetaInfo = serviceMetaInfoList.get(0);

            // 发送请求 (HTTP)
//            try (HttpResponse httpResponse = HttpRequest
//                    .post(selectMetaInfo.getServiceAddress())
//                    .body(bytes).execute()) {
//                result = httpResponse.bodyBytes();
//            }
//            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);

            // 发送请求 (TCP)
            // 发送 TCP 请求
            Vertx vertx = Vertx.vertx();
            NetClient netClient = vertx.createNetClient();
            CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
            netClient.connect(selectMetaInfo.getServicePort(), selectMetaInfo.getServiceHost(),
                    result -> {
                        if (result.succeeded()) {
                            System.out.println("Connected to TCP server");
                            io.vertx.core.net.NetSocket socket = result.result();
                            // 发送数据
                            // 构造消息
                            ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                            ProtocolMessage.Header header = new ProtocolMessage.Header();
                            header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                            header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                            header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                            header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                            header.setRequestId(IdUtil.getSnowflakeNextId());
                            protocolMessage.setHeader(header);
                            protocolMessage.setBody(rpcRequest);
                            // 编码请求
                            try {
                                Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
                                socket.write(encodeBuffer);
                            } catch (IOException e) {
                                throw new RuntimeException("协议消息编码错误");
                            }

                            // 接收响应
                            socket.handler(buffer -> {
                                try {
                                    ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                                    responseFuture.complete(rpcResponseProtocolMessage.getBody());
                                } catch (IOException e) {
                                    throw new RuntimeException("协议消息解码错误");
                                }
                            });
                        } else {
                            System.err.println("Failed to connect to TCP server");
                        }
                    });

            RpcResponse rpcResponse = responseFuture.get();
            // 记得关闭连接
            netClient.close();

            System.out.println("本次调用的信息 " + rpcResponse.getMessage());
            System.out.println("返回参数的类型" + rpcResponse.getDataType());
            return rpcResponse.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
