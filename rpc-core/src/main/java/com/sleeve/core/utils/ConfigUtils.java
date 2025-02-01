package com.sleeve.core.utils;


import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;

/**
 * 读取配置文件信息，并返回配置对象
 */
public class ConfigUtils {


    /**
     * 加载配置对象，默认对象
     * @param tClass
     * @param prefix
     * @return
     * @param <T>
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix) {
        return loadConfig(tClass, prefix, "");
    }

    /**
     * 加载配置文件，支持区分环境
     * @param tClass
     * @param prefix
     * @param environment
     * @return
     * @param <T>
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix, String environment) {
        StringBuilder builder = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)) {
            builder.append("-").append(environment);
        }
        builder.append(".properties");
        // 通过配置文件名称，获取配置文件对象
        Props props = new Props(builder.toString());
        // 转化为指定类对象，包含前缀为prefix的内容
        return props.toBean(tClass, prefix);
    }
}
