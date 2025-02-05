package com.sleeve.core.protocol;


import lombok.Getter;

/**
 * 协议消息的状态类型枚举
 */
@Getter
public enum ProtocolMesageStatusEnum {

    OK("ok", 20),
    BAD_REQUEST("badRequest", 40),
    BAD_RESPONSE("badResponse", 50);

    private final String text;

    private final int value;

    ProtocolMesageStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获得枚举值
     * @param value 传入 { 20 ==> OK("ok", 20) }
     * @return
     */
    public static ProtocolMesageStatusEnum getEnumByValue(int value) {
        for (ProtocolMesageStatusEnum statusEnum : ProtocolMesageStatusEnum.values()) {
            if (statusEnum.value == value) {
                return statusEnum;
            }
        }
        return null;
    }
}
