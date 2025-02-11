package com.sleeve.rpc.protocol;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 自定义协议传递的消息形式
 * @param <T>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProtocolMessage<T> {


    /**
     * 消息头
     */
    private Header header;

    /**
     * 消息体 (请求或响应对象)
     */
    private T body;


    /**
     * 协议请求头
     */
    @Data
    public static class Header {
        /**
         * 魔数，保证数据安全性
         */
        private byte magic;

        /**
         * 版本号
         */
        private byte version;

        /**
         * 序列化器
         */
        private byte serializer;

        /**
         * 消息类型 (请求 / 响应)
         */
        private byte type;

        /**
         * 状态
         */
        private byte status;

        /**
         * 请求ID
         */
        private long requestId;

        /**
         * 消息体长度
         */
        private int bodyLength;
   }
}
