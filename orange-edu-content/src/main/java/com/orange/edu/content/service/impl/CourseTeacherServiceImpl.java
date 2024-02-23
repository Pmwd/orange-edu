package com.orange.edu.content.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orange.base.exception.OrangeEduException;
import com.orange.edu.content.mapper.CourseTeacherMapper;
import com.orange.edu.content.model.po.CourseTeacher;
import com.orange.edu.content.service.CourseTeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {
    @Autowired
    private CourseTeacherMapper courseTeacherMapper;


    /**
     * 查询课程老师列表
     *
     * @param id id
     * @return {@link List}<{@link CourseTeacher}>
     */
    @Override
    public List<CourseTeacher> queryCourseTeacherList(Long id) {
        LambdaQueryWrapper<CourseTeacher> qw = new LambdaQueryWrapper<>();
        qw.eq(CourseTeacher::getCourseId, id);
        return courseTeacherMapper.selectList(qw);
    }

    /**
     * 保存与修改课程老师
     *
     * @param courseTeacher 课程老师
     * @return {@link CourseTeacher}
     */
    @Override
    public CourseTeacher saveCourseTeacher(CourseTeacher courseTeacher) {
        //传id即为修改
        if (courseTeacher.getId() == null) {
            //数据库中设计为课程id与教师姓名为唯一字段，要进行判断不然会报错
            LambdaQueryWrapper<CourseTeacher> qw = new LambdaQueryWrapper<>();
            qw.eq(CourseTeacher::getCourseId, courseTeacher.getCourseId())
                    .eq(CourseTeacher::getTeacherName, courseTeacher.getTeacherName());
            CourseTeacher isCourseTeacher = courseTeacherMapper.selectOne(qw);
            if(isCourseTeacher!=null){
                OrangeEduException.cast("已有该教师!");
            }
            //插入教师信息
            int insert = courseTeacherMapper.insert(courseTeacher);
            if (insert <= 0) {
                OrangeEduException.cast("保存教师信息失败");
            }
            return courseTeacherMapper.selectById(courseTeacher);
        } else {
            //更新教师信息
            int i = courseTeacherMapper.updateById(courseTeacher);
            if (i <= 0) {
                OrangeEduException.cast("更新教师失败");
            }
            return courseTeacherMapper.selectById(courseTeacher);
        }
    }

    /**
     * 删除课程老师
     *
     * @param courseId  课程id
     * @param teacherId 老师id
     */
    @Override
    public void deleteCourseTeacher(Long courseId, Long teacherId) {
        LambdaQueryWrapper<CourseTeacher> qw = new LambdaQueryWrapper<>();
        qw.eq(CourseTeacher::getCourseId, courseId)
                .eq(CourseTeacher::getId,teacherId);
        int delete = courseTeacherMapper.delete(qw);
        if (delete<=0) {
            OrangeEduException.cast("删除教师失败");
        }
    }

}

