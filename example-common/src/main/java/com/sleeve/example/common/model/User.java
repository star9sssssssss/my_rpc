package com.sleeve.example.common.model;

import java.io.Serializable;

// 实现序列化接口, 为以后的传输提供支持
public class User implements Serializable {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }
}
