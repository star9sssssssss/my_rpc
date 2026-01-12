package com.sleeve.nettyrpc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * 将 RpcResponse对象编码为字节数组
 */
public class RpcResponseEncoder extends MessageToByteEncoder<RpcResponse> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse, ByteBuf byteBuf) throws Exception {
        byte[] magic = Message.MAGIC;
        byte messageType = Message.MessageType.REQUEST.getCode();
        byte[] body = serializeRpcResponse(rpcResponse);
        int length = magic.length + Byte.BYTES + body.length;
        byteBuf.writeInt(length);
        byteBuf.writeBytes(magic);
        byteBuf.writeByte(messageType);
        byteBuf.writeBytes(body);
    }

    private byte[] serializeRpcResponse(RpcResponse rpcResponse) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(rpcResponse);
        objectOutputStream.close();
        return outputStream.toByteArray();
    }
}
