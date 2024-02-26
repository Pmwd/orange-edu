package com.orange.edu.ucenter.model.dto;

import com.orange.edu.ucenter.model.po.OeUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户扩展信息
 *
 * @author Wuxy
 * @version 1.0
 * @since 2022/9/30 13:56
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OeUserExt extends OeUser {
    /**
     * 用户权限
     */
    List<String> permissions = new ArrayList<>();

}
