package com.sleeve.easyrpc.server.netty;

import com.sleeve.easyrpc.model.RpcResponse;
import com.sleeve.easyrpc.registry.LocalRegistry;
import com.sleeve.easyrpc.serializer.JdkSerializer;
import com.sleeve.easyrpc.serializer.Serializer;
import com.sleeve.easyrpc.model.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.lang.reflect.Method;

public class NettyRpcRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final Serializer serializer = new JdkSerializer(); // 假设你已有该类

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        System.out.println("Received request: " + request.method() + " " + request.uri());

        // 只处理 POST 请求（RPC 通常用 POST）
        if (request.method() != HttpMethod.POST) {
            sendErrorResponse(ctx, "Only POST method is supported");
            return;
        }

        // 获取请求体字节数组
        byte[] bodyBytes = new byte[request.content().readableBytes()];
        request.content().readBytes(bodyBytes);

        RpcRequest rpcRequest = null;
        try {
            rpcRequest = serializer.deserialize(bodyBytes, RpcRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
            sendErrorResponse(ctx, "Failed to deserialize request: " + e.getMessage());
            return;
        }

        // 构造响应
        RpcResponse rpcResponse = new RpcResponse();
        if (rpcRequest == null) {
            rpcResponse.setMessage("rpcRequest is null");
            sendRpcResponse(ctx, rpcResponse);
            return;
        }

        try {
            // 2. 从本地注册表获取实现类
            Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
            if (implClass == null) {
                rpcResponse.setMessage("Service not found: " + rpcRequest.getServiceName());
                sendRpcResponse(ctx, rpcResponse);
                return;
            }

            // 3. 反射调用
            Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
            Object result = method.invoke(implClass.getDeclaredConstructor().newInstance(), rpcRequest.getArgs());

            rpcResponse.setData(result);
            rpcResponse.setDataType(method.getReturnType());
            rpcResponse.setMessage("OK");
        } catch (Exception e) {
            e.printStackTrace();
            rpcResponse.setMessage(e.toString());
            rpcResponse.setException(e);
        }

        // 4. 发送响应
        sendRpcResponse(ctx, rpcResponse);
    }

    private void sendRpcResponse(ChannelHandlerContext ctx, RpcResponse response) {
        try {
            byte[] bytes = serializer.serialize(response);
            FullHttpResponse httpResponse = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(bytes)
            );
            httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/octet-stream");
            httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, bytes.length);

            ctx.writeAndFlush(httpResponse);
        } catch (IOException e) {
            e.printStackTrace();
            sendErrorResponse(ctx, "Failed to serialize response");
        }
    }

    private void sendErrorResponse(ChannelHandlerContext ctx, String message) {
        FullHttpResponse response = new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1,
            HttpResponseStatus.BAD_REQUEST,
            Unpooled.copiedBuffer(message.getBytes())
        );
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}