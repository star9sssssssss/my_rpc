package com.sleeve.nettyrpc.message;

import lombok.Data;

import java.nio.charset.StandardCharsets;

@Data
public class Message {

    public static final byte[] MAGIC = "sleeve".getBytes(StandardCharsets.UTF_8);

    private byte[] magic;

    private byte messageType;

    private byte[] body;

    public enum MessageType {
        REQUEST(1),
        RESPONSE(2);

        private final byte code;

        MessageType(int code) {
            this.code = (byte) code;
        }

        public byte getCode() {
            return code;
        }
    }
}
