package com.orange.edu.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orange.edu.content.model.dto.TeachplanDto;
import com.orange.edu.content.model.po.Teachplan;

import java.util.List;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author pmwd
 */
public interface TeachplanMapper extends BaseMapper<Teachplan> {

    /**
     * @description 查询某课程的课程计划，组成树型结构
     * @param courseId
     * @return
     */
    public List<TeachplanDto> selectTreeNodes(long courseId);
}
