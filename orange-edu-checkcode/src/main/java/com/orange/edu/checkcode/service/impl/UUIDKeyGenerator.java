package com.orange.edu.checkcode.service.impl;


import com.orange.edu.checkcode.service.CheckCodeService;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * uuid生成器
 *
 */
@Component("UUIDKeyGenerator")
public class UUIDKeyGenerator implements CheckCodeService.KeyGenerator {
    @Override
    public String generate(String prefix) {
        String uuid = UUID.randomUUID().toString();
        return prefix + uuid.replaceAll("-", "");
    }
}
