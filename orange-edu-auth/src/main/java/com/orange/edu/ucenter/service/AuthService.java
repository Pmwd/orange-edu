package com.orange.edu.ucenter.service;


import com.orange.edu.ucenter.model.dto.AuthParamsDto;
import com.orange.edu.ucenter.model.dto.OeUserExt;

/**
 * 认证 service
 *
 *
 */
public interface AuthService {

    /**
     * 认证方法
     *
     * @param authParamsDto 认证参数
     *
     */
    OeUserExt execute(AuthParamsDto authParamsDto);

}
