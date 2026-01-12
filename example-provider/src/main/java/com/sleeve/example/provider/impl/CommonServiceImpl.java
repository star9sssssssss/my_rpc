package com.sleeve.example.provider.impl;

import com.sleeve.example.common.service.CommonService;

public class CommonServiceImpl implements CommonService {
    @Override
    public int add(int a, int b) {
        return a + b + 100;
    }
}
