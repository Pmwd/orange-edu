package com.orange.edu.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orange.edu.ucenter.feignclient.CheckCodeClient;
import com.orange.edu.ucenter.mapper.OeUserMapper;
import com.orange.edu.ucenter.model.dto.AuthParamsDto;
import com.orange.edu.ucenter.model.dto.OeUserExt;
import com.orange.edu.ucenter.model.po.OeUser;
import com.orange.edu.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 基于账号密码对比的认证实现类
 *
 *
 */
@Slf4j
@Service("password_authservice")
public class PasswordAuthServiceImpl implements AuthService {
    @Resource
    private OeUserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CheckCodeClient checkCodeClient;

    @Override
    public OeUserExt execute(AuthParamsDto authParamsDto) {
        // 校验验证码
        String checkcode = authParamsDto.getCheckcode();
        String checkcodekey = authParamsDto.getCheckcodekey();

        if (StringUtils.isBlank(checkcodekey) || StringUtils.isBlank(checkcode)) {
            throw new RuntimeException("验证码为空");
        }

        if (!checkCodeClient.verify(checkcodekey, checkcode)) {
            throw new RuntimeException("验证码输入错误");
        }

        // 账号
        String username = authParamsDto.getUsername();
        // 查询用户
        OeUser user = userMapper.selectOne(new LambdaQueryWrapper<OeUser>().eq(OeUser::getUsername, username));
        if (user == null) {
            // 用户不存在
            throw new RuntimeException("账号不存在");
        }
        // 取出数据库存储的正确密码
        String password = user.getPassword(); // 加密后的正确密码
        String inputPwd = authParamsDto.getPassword(); // 输入的密码
        // 比对密码
        if (!passwordEncoder.matches(inputPwd, password)) {
            throw new RuntimeException("账号或密码错误");
        }
        OeUserExt xcUserExt = new OeUserExt();
        BeanUtils.copyProperties(user, xcUserExt);
        return xcUserExt;
    }
}
