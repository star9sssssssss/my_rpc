package com.sleeve.nettyrpc.codec;


import com.sleeve.nettyrpc.message.Message;
import com.sleeve.nettyrpc.message.RpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * 将 RpcRequest对象编码为字节数组
 */
public class RpcRequestEncoder extends MessageToByteEncoder<RpcRequest> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcRequest rpcRequest, ByteBuf byteBuf) throws Exception {
        byte[] magic = Message.MAGIC;
        byte messageType = Message.MessageType.REQUEST.getCode();
        byte[] body = serializeRpcRequest(rpcRequest);
        int length = magic.length + Byte.BYTES + body.length;
        byteBuf.writeInt(length);
        byteBuf.writeBytes(magic);
        byteBuf.writeByte(messageType);
        byteBuf.writeBytes(body);
    }

    private byte[] serializeRpcRequest(RpcRequest rpcRequest) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(rpcRequest);
        objectOutputStream.close();
        return outputStream.toByteArray();
    }
}
