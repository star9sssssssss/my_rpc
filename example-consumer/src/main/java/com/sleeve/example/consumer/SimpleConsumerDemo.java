package com.sleeve.example.consumer;

import com.sleeve.example.common.model.User;
import com.sleeve.example.common.service.CommonService;
import com.sleeve.example.common.service.UserService;
import com.sleeve.easyrpc.proxy.ServiceProxyFactory;

public class SimpleConsumerDemo {
    public static void main(String[] args) {
        CommonService service = ServiceProxyFactory.getProxy(CommonService.class);
        System.out.println(service.add(100, 200));
    }
}
