package com.orange.edu.content.service;

import com.orange.edu.content.model.dto.BindTeachplanMediaDto;
import com.orange.edu.content.model.dto.SaveTeachplanDto;
import com.orange.edu.content.model.dto.TeachplanDto;
import com.orange.edu.content.model.po.TeachplanMedia;

import java.util.List;

public interface TeachplanService {

    /**
     * @description 查询课程计划树型结构
     * @param courseId  课程id
     * @return List<TeachplanDto>
     */
    public List<TeachplanDto> findTeachplanTree(long courseId);

    /**
     * @description 只在课程计划
     * @param teachplanDto  课程计划信息
     *
     */
    public void saveTeachplan(SaveTeachplanDto teachplanDto);

    void deleteTeachplan(Long id);

    void movedownTeachplan(Long id);

    void moveupTeachplan(Long id);

    /**
     * @description 教学计划绑定媒资
     * @param bindTeachplanMediaDto
     *
     */
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);

    void deleteTeachplanMedia(Long teachPlanId, String mediaId);
}

