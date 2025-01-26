package com.sleeve.example.consumer;

import com.sleeve.example.common.model.User;
import com.sleeve.example.common.service.UserService;
import com.sleeve.rpc.proxy.ServiceProxyFactory;

/**
 * 简单的消费者示例
 */
public class EasyConsumerExample {

    // 静态代理模式
//    public static void main(String[] args) {
//        UserServiceProxy serviceProxy = new UserServiceProxy();
//        User user = new User();
//        user.setName("Jack");
//        User user1 = serviceProxy.getUser(user);
//        System.out.println(user1);
//    }

    public static void main(String[] args) {
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("Jack");
        System.out.println(userService.getUser(user));
    }
}
