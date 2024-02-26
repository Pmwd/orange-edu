package com.orange.edu.ucenter.service;

import com.orange.edu.ucenter.model.po.OeUser;

public interface WxAuthService {
    public OeUser wxAuth(String code);
}
