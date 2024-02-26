package com.orange.edu.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orange.edu.ucenter.mapper.OeUserMapper;
import com.orange.edu.ucenter.mapper.OeUserRoleMapper;
import com.orange.edu.ucenter.model.dto.AuthParamsDto;
import com.orange.edu.ucenter.model.dto.OeUserExt;
import com.orange.edu.ucenter.model.po.OeUser;
import com.orange.edu.ucenter.model.po.OeUserRole;
import com.orange.edu.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 微信扫码认证
 *
 *
 */
@Slf4j
@Service("wx_authservice")
public class WxAuthServiceImpl implements AuthService {
    @Resource
    private OeUserMapper userMapper;

    @Resource
    private OeUserRoleMapper userRoleMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WxAuthServiceImpl currentProxy;

    @Value("${weixin.appid}")
    private String appid;

    @Value("${weixin.secret}")
    private String secret;


    /**
     * 微信扫码认证，不校验验证码，不校验密码
     *
     * @param authParamsDto 认证参数
     *
     */
    @Override
    public OeUserExt execute(AuthParamsDto authParamsDto) {
        // 账号
        String username = authParamsDto.getUsername();
        OeUser user = userMapper.selectOne(new LambdaQueryWrapper<OeUser>().eq(OeUser::getUsername, username));
        if (user == null) {
            throw new RuntimeException("账号不存在");
        }
        OeUserExt oeUserExt = new OeUserExt();
        BeanUtils.copyProperties(user, oeUserExt);
        return oeUserExt;
    }


    public OeUser wxAuth(String code){

        //收到code调用微信接口申请access_token
        Map<String, String> access_token_map = getAccess_token(code);
        if(access_token_map==null){
            return null;
        }
        System.out.println(access_token_map);
        String openid = access_token_map.get("openid");
        String access_token = access_token_map.get("access_token");
        //拿access_token查询用户信息
        Map<String, String> userinfo = getUserinfo(access_token, openid);
        if(userinfo==null){
            return null;
        }
        //将用户信息保存到数据库
        OeUser oeUser = currentProxy.addWxUser(userinfo);
        return oeUser;
    }


    @Transactional
    public OeUser addWxUser(Map userInfo_map){
        String unionid = userInfo_map.get("unionid").toString();
        //根据unionid查询数据库
        OeUser xcUser = userMapper.selectOne(new LambdaQueryWrapper<OeUser>().eq(OeUser::getWxUnionid, unionid));
        if(xcUser!=null){
            return xcUser;
        }
        String userId = UUID.randomUUID().toString();
        xcUser = new OeUser();
        xcUser.setId(userId);
        xcUser.setWxUnionid(unionid);
        //记录从微信得到的昵称
        xcUser.setNickname(userInfo_map.get("nickname").toString());
        xcUser.setUserpic(userInfo_map.get("headimgurl").toString());
        xcUser.setName(userInfo_map.get("nickname").toString());
        xcUser.setUsername(unionid);
        xcUser.setPassword(unionid);
        xcUser.setUtype("101001");//学生类型
        xcUser.setStatus("1");//用户状态
        xcUser.setCreateTime(LocalDateTime.now());
        userMapper.insert(xcUser);
        OeUserRole xcUserRole = new OeUserRole();
        xcUserRole.setId(UUID.randomUUID().toString());
        xcUserRole.setUserId(userId);
        xcUserRole.setRoleId("17");//学生角色
        userRoleMapper.insert(xcUserRole);
        return xcUser;
    }


    /**
     * 申请访问令牌,响应示例
     {
     "access_token":"ACCESS_TOKEN",
     "expires_in":7200,
     "refresh_token":"REFRESH_TOKEN",
     "openid":"OPENID",
     "scope":"SCOPE",
     "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL"
     }
     */
    private Map<String,String> getAccess_token(String code) {

        String wxUrl_template = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
        //请求微信地址
        String wxUrl = String.format(wxUrl_template, appid, secret, code);

        log.info("调用微信接口申请access_token, url:{}", wxUrl);

        ResponseEntity<String> exchange = restTemplate.exchange(wxUrl, HttpMethod.POST, null, String.class);

        String result = exchange.getBody();
        log.info("调用微信接口申请access_token: 返回值:{}", result);
        Map<String,String> resultMap = JSON.parseObject(result, Map.class);

        return resultMap;
    }

    /**获取用户信息，示例如下：
     {
     "openid":"OPENID",
     "nickname":"NICKNAME",
     "sex":1,
     "province":"PROVINCE",
     "city":"CITY",
     "country":"COUNTRY",
     "headimgurl": "https://thirdwx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0",
     "privilege":[
     "PRIVILEGE1",
     "PRIVILEGE2"
     ],
     "unionid": " o6_bmasdasdsad6_2sgVt7hMZOPfL"
     }
     */
    private Map<String,String> getUserinfo(String access_token,String openid) {

        String wxUrl_template = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s";
        //请求微信地址
        String wxUrl = String.format(wxUrl_template, access_token,openid);

        log.info("调用微信接口申请access_token, url:{}", wxUrl);

        ResponseEntity<String> exchange = restTemplate.exchange(wxUrl, HttpMethod.POST, null, String.class);

        //防止乱码进行转码
        String result = new     String(exchange.getBody().getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8);
        log.info("调用微信接口申请access_token: 返回值:{}", result);
        Map<String,String> resultMap = JSON.parseObject(result, Map.class);

        return resultMap;
    }

}
