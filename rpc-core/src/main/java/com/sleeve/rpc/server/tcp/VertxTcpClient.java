package com.sleeve.rpc.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.sleeve.rpc.RpcApplication;
import com.sleeve.rpc.model.RpcRequest;
import com.sleeve.rpc.model.RpcResponse;
import com.sleeve.rpc.model.ServiceMetaInfo;
import com.sleeve.rpc.protocol.*;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class VertxTcpClient {

    public void test_start() {

        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();

        // 连接服务端
        netClient.connect(8888, "localhost", result -> {
            if (result.succeeded()) {
                System.out.println("Connected to TCP server");
                io.vertx.core.net.NetSocket socket = result.result();
                for (int i = 0; i < 10; i++) {
                    // 发送数据
                    Buffer buffer = Buffer.buffer();
                    String str = "Hello, server!Hello, server!Hello, server!Hello, server!";
                    buffer.appendInt(0);
                    buffer.appendInt(str.getBytes().length);
                    buffer.appendBytes(str.getBytes());
//                    System.out.println(buffer.toString(StandardCharsets.UTF_8));
                    socket.write(buffer);
                }
                // 接收响应
                socket.handler(buffer -> {
                    System.out.println("Received response from server: " + buffer.toString());
                });
            } else {
                System.err.println("Failed to connect to TCP server");
            }
        });

    }

    /**
     * TCP连接的客户端
     * 发送请求
     * 接收响应
     * 将原生请求包装成自定义协议请求ProtocolMessage，并将自定义协议响应ProtocolMessage解包为的原生响应
     * @param rpcRequest 需要发送的原生请求
     * @param serviceMetaInfo 调用的远程服务信息
     * @return 本次请求的原生响应
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws InterruptedException, ExecutionException {
        Vertx vertx = Vertx.vertx();
        // 1. 创建Tcp客户端
        NetClient netClient = vertx.createNetClient();
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        netClient.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceHost(),
                result -> {
                    if (!result.succeeded()) {
                        System.err.println("Failed to connect to TCP server");
                        return;
                    }
                    NetSocket socket = result.result();
                    // 2.构造协议的消息类型 ProtocolMessage<RpcRequest>
                    ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                    ProtocolMessage.Header header = new ProtocolMessage.Header();
                    header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                    header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                    header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                    header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                    header.setRequestId(IdUtil.getSnowflakeNextId());
                    protocolMessage.setHeader(header);
                    protocolMessage.setBody(rpcRequest);

                    // 3.将消息对象转换为Tcp支持传输的对象类型Buffer，发送请求
                    try {
                        Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
                        socket.write(encodeBuffer);
                    } catch (IOException e) {
                        throw new RuntimeException("协议消息编码错误");
                    }

                    // 4.使用包装类封装处理响应，解码为原生响应对象ProtocolMessage<RpcResponse>，使用异步任务等待响应结果
                    Handler<Buffer> bufferHandler = new Handler<>() {
                        @Override
                        public void handle(Buffer buffer) {
                            try {
                                ProtocolMessage<RpcResponse> rpcResponseProtocolMessage =
                                        (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                                responseFuture.complete(rpcResponseProtocolMessage.getBody());
                            } catch (IOException e) {
                                throw new RuntimeException("协议消息解码错误");
                            }
                        }
                    };
                    // 经过 "装饰" 后的处理类
                    TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(bufferHandler);
                    // 接收数据进行解码
                    socket.handler(bufferHandlerWrapper);
                });
        // 5.获得最终的响应结果，关闭本次Tcp连接
        RpcResponse rpcResponse = responseFuture.get();
        netClient.close();
        return rpcResponse;
    }


    public static void main(String[] args) {
        new VertxTcpClient().test_start();
    }


}

