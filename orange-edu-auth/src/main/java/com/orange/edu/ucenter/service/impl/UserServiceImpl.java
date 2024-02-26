package com.orange.edu.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.orange.edu.ucenter.mapper.OeMenuMapper;
import com.orange.edu.ucenter.model.dto.AuthParamsDto;
import com.orange.edu.ucenter.model.dto.OeUserExt;
import com.orange.edu.ucenter.model.po.OeMenu;
import com.orange.edu.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义 UserDetailsService 用来对接 Spring Security
 *
 */
@Slf4j
@Service
public class UserServiceImpl implements UserDetailsService {
    @Autowired
    private ApplicationContext applicationContext;

    @Resource
    private OeMenuMapper menuMapper;


    /**
     * 查询用户信息组成用户身份信息
     *
     * @param authParamsDtoJson 类型的 json 数据
     * @return {@link UserDetails}
     * @throws UsernameNotFoundException 用户名未找到异常
     */
    @Override
    public UserDetails loadUserByUsername(String authParamsDtoJson) throws UsernameNotFoundException {
        AuthParamsDto authParamsDto;
        try {
            // 将认证参数转为 AuthParamsDto 类型
            authParamsDto = JSON.parseObject(authParamsDtoJson, AuthParamsDto.class);
        } catch (Exception e) {
            log.info("认证请求不符合项目要求:{}", authParamsDtoJson);
            throw new RuntimeException("认证请求数据格式不对");
        }
        // 认证方式
        String authType = authParamsDto.getAuthType();
        // 从 spring 容器中拿到具体的认真实例
        AuthService authService = applicationContext.getBean(authType + "_authservice", AuthService.class);
        // 开始认证
        OeUserExt xcUserExt = authService.execute(authParamsDto);

        return getUserPrincipal(xcUserExt);
    }

    /**
     * 根据 OeUserExt 对象构造一个 UserDetails 对象
     *
     * @param user userExt 对象-用户信息
     * @return {@link UserDetails}
     */
    public UserDetails getUserPrincipal(OeUserExt user) {
        String password = user.getPassword();
        //查询用户权限
        List<OeMenu> xcMenus = menuMapper.selectPermissionByUserId(user.getId());
        List<String> permissions = new ArrayList<>();
        if(xcMenus.size()<=0){
            //用户权限,如果不加则报Cannot pass a null GrantedAuthority collection
            permissions.add("p1");
        }else{
            xcMenus.forEach(menu->{
                permissions.add(menu.getCode());
            });
        }
        //将用户权限放在XcUserExt中
        user.setPermissions(permissions);

        //为了安全在令牌中不放密码
        user.setPassword(null);
        //将user对象转json
        String userString = JSON.toJSONString(user);
        String[] authorities = permissions.toArray(new String[0]);
        UserDetails userDetails = User.withUsername(userString).password(password).authorities(authorities).build();
        return userDetails;

    }
}
