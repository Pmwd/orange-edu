package com.orange.edu.ucenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orange.edu.ucenter.model.po.OeMenu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author pmwd
 */
public interface OeMenuMapper extends BaseMapper<OeMenu> {

    /**
     * 查询指定用户的所有权限
     *
     * @param userId 用户id
     * @return 权限列表
     */
    @Select("SELECT	* FROM oe_menu WHERE id IN (SELECT menu_id FROM oe_permission WHERE role_id IN ( SELECT role_id FROM oe_user_role WHERE user_id = #{userId} ))")
    List<OeMenu> selectPermissionByUserId(@Param("userId") String userId);
}
