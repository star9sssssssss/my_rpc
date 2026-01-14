package com.sleeve.nettyrpc.codec;

import com.sleeve.nettyrpc.message.Message;
import com.sleeve.nettyrpc.message.RpcRequest;
import com.sleeve.nettyrpc.message.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.Arrays;
import java.util.IllformedLocaleException;
import java.util.Objects;

/**
 * 自定义的解码器
 */
public class BusinessDecoder extends LengthFieldBasedFrameDecoder {

    public BusinessDecoder() {
        super(1024*1024, 0, 4, 0, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        // 1. 获得魔数
        byte[] magic = new byte[Message.MAGIC.length];
        frame.readBytes(magic);
        if (!Arrays.equals(magic, Message.MAGIC)) {
            throw new IllformedLocaleException("魔数错误! 协议存在问题");
        }
        // 2. 读取消息的类型
        byte messageType = frame.readByte();
        // 3. 读取消息体,即真实的数据
        byte[] body = new byte[frame.readableBytes()];
        frame.readBytes(body);
        // 4. 请求消息, 反序列化请求体
        if (Objects.equals(Message.MessageType.REQUEST.getCode(), messageType)) {
            return deserializerRequest(body);
        }
        // 5. 响应消息, 反序列化响应体
        if (Objects.equals(Message.MessageType.RESPONSE.getCode(), messageType)) {
            return deserializerResponse(body);
        }
        throw new IllformedLocaleException("消息类型不支持");
    }

    private RpcRequest deserializerRequest(byte[] body) {
        return new RpcRequest();
    }

    private RpcResponse deserializerResponse(byte[] body) {
        return new RpcResponse();
    }
}
