package com.sleeve.example.provider;

import com.sleeve.example.common.model.User;
import com.sleeve.example.common.service.UserService;

/**
 * 用户服务实现类
 */
public class UserServiceImpl implements UserService {


    public User getUser(User user) {
        System.out.println("当前用户用户名为: " + user.getName());
        return user;
    }
}
