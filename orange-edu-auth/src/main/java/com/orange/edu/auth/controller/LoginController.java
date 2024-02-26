package com.orange.edu.auth.controller;

import com.orange.edu.ucenter.mapper.OeUserMapper;
import com.orange.edu.ucenter.model.po.OeUser;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 测试controller
 *
 *
 */
@Slf4j
@RestController
public class LoginController {
    @Resource
    OeUserMapper userMapper;


    @RequestMapping("/login-success")
    public String loginSuccess() {
        return "登录成功";
    }

    @RequestMapping("/user/{id}")
    public OeUser getuser(@PathVariable("id") String id) {
        return userMapper.selectById(id);
    }

    @RequestMapping("/r/r1")
    @PreAuthorize("hasAuthority('p1')") // 拥有 p1 权限方可访问
    public String r1() {
        return "访问r1资源";
    }

    @RequestMapping("/r/r2")
    @PreAuthorize("hasAuthority('p2')") // 拥有 p2 权限方可访问
    public String r2() {
        return "访问r2资源";
    }

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public String register() {

        return "success";
    }

}
