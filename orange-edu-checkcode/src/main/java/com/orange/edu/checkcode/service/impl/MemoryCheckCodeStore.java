package com.orange.edu.checkcode.service.impl;


import com.orange.edu.checkcode.service.CheckCodeService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 使用本地内存存储验证码，测试用
 *
 *
 */
@Component("MemoryCheckCodeStore")
public class MemoryCheckCodeStore implements CheckCodeService.CheckCodeStore {
    Map<String, String> map = new HashMap<>();

    @Override
    public void set(String key, String value, Integer expire) {
        map.put(key, value);
    }

    @Override
    public String get(String key) {
        return map.get(key);
    }

    @Override
    public void remove(String key) {
        map.remove(key);
    }
}
