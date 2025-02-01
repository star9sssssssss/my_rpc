package com.sleeve.core.serializer;


import com.sleeve.core.spi.SpiLoader;


/**
 *序列化器工厂，序列化器可以复用，不用每次都进行创建，使用工厂模式和单例模式简化操作
 */
public class SerializerFactory {

    // 使用读取配置文件方式
    static {
        SpiLoader.load(Serializer.class);
    }


    /**
     * 序列化映射，用于实现单例模式
     */
//    public static final Map<String, Serializer> KEY_SERIALIZER_MAP = new HashMap<>(){{
//        put(SerializerKeys.JDK, new JdkSerializer());
//        put(SerializerKeys.JSON, new JsonSerializer());
//        put(SerializerKeys.KRYO, new KryoSerializer());
//        put(SerializerKeys.HESSIAN, new HessianSerializer());
//    }};

    // 默认的序列化器
    public static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();


    /**
     * 获取实例
     * @param key
     * @return
     */
    public static Serializer getInstance(String key) {
        return SpiLoader.getInstance(Serializer.class, key);
    }

}
