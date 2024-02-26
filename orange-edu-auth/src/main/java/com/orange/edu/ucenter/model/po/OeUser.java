package com.orange.edu.ucenter.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author pmwd
 */
@Data
@TableName("oe_user")
public class OeUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String username;

    private String password;

    private String salt;

    /**
     * 微信unionid
     */
    private String wxUnionid;

    /**
     * 昵称
     */
    private String nickname;

    private String name;

    /**
     * 头像
     */
    private String userpic;

    private String companyId;

    private String utype;

    private LocalDateTime birthday;

    private String sex;

    private String email;

    private String cellphone;

    private String qq;

    /**
     * 用户状态
     */
    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
