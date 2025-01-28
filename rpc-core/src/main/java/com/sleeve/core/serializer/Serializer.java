package com.sleeve.core.serializer;

import java.io.IOException;

/**
 * 实现数据的序列化和反序列化
 */
public interface Serializer {

    /**
     * 序列化: 将java对象转换为字节数组
     * @param object java对象
     * @return 字节数组
     * @param <T> 对象类型
     * @throws IOException 异常
     */
    <T> byte[] serialize(T object) throws IOException;


    /**
     * 反序列化: 将字节数组转换为java对象
     * @param bytes 字节数组
     * @param type 转换的对象的class类型
     * @return 转换后的对象
     * @param <T> 对象类型
     * @throws IOException 异常
     */
    <T> T deserialize(byte[] bytes, Class<T> type) throws IOException;

}
