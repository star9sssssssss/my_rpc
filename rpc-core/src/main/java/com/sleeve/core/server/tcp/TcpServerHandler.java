package com.sleeve.core.server.tcp;

import com.sleeve.core.model.RpcRequest;
import com.sleeve.core.model.RpcResponse;
import com.sleeve.core.protocol.ProtocolMessage;
import com.sleeve.core.protocol.ProtocolMessageDecoder;
import com.sleeve.core.protocol.ProtocolMessageEncoder;
import com.sleeve.core.protocol.ProtocolMessageTypeEnum;
import com.sleeve.core.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * TCP 请求处理器
 *
 */
public class TcpServerHandler implements Handler<NetSocket> {

    /**
     * TCP连接的服务端
     * 接收请求
     * 返回响应
     * @param netSocket
     */
    @Override
    public void handle(NetSocket netSocket) {

        TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
            // 1.对请求进行解码为ProtocolMessage<RpcRequest>
            ProtocolMessage<RpcRequest> protocolMessage;
            try {
                protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
            } catch (IOException e) {
                throw new RuntimeException("协议消息解码错误");
            }
            // 2.获得原生请求RpcRequest，调用服务，返回原生响应
            RpcRequest rpcRequest = protocolMessage.getBody();
            RpcResponse rpcResponse = new RpcResponse();
            Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
            try {
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            // 3.将原生响应封装为ProtocolMessage<RpcResponse>并编码为Buffer类型，进行Tcp传输
            ProtocolMessage.Header header = protocolMessage.getHeader();
            // 只改变头部的消息类型，其余的都不进行修改
            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
            ProtocolMessage<RpcResponse> responseProtocolMessage =
                    new ProtocolMessage<>(header, rpcResponse);
            try {
                Buffer encode = ProtocolMessageEncoder.encode(responseProtocolMessage);
                netSocket.write(encode);
            } catch (IOException e) {
                throw new RuntimeException("协议消息编码错误");
            }
        });

        // 处理连接, 接收请求
        netSocket.handler(bufferHandlerWrapper);
    }
}
