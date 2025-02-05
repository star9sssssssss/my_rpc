package com.sleeve.core.protocol;

import com.sleeve.core.serializer.Serializer;
import com.sleeve.core.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * 编码器
 */
public class ProtocolMessageEncoder {

    /**
     * 编码
     * @param protocolMessage 协议消息类
     * @return
     * @throws IOException
     */
    public static Buffer encode(ProtocolMessage<?> protocolMessage) throws IOException {
        if (protocolMessage == null || protocolMessage.getHeader() == null) {
            return Buffer.buffer();
        }
        ProtocolMessage.Header header = protocolMessage.getHeader();
        // 依次向缓冲区写入字节
        Buffer buffer = Buffer.buffer();
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getSerializer());
        buffer.appendByte(header.getType());
        buffer.appendByte(header.getStatus());
        buffer.appendLong(header.getRequestId());
        // 获取序列化器
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum
                .getEnumByKey(header.getSerializer());
        if (serializerEnum == null) {
            throw new RuntimeException("序列化协议不存在");
        }
        Serializer mySerializer = SerializerFactory.getInstance(serializerEnum.getValue());
        byte[] bodyBytes = mySerializer.serialize(protocolMessage.getBody());
        // 写入body长度和数据
        buffer.appendInt(bodyBytes.length);
        buffer.appendBytes(bodyBytes);
        return buffer;
    }
}
