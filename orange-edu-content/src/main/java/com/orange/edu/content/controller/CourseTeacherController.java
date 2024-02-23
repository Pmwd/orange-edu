package com.orange.edu.content.controller;

import com.orange.base.exception.ValidationGroups;
import com.orange.edu.content.model.po.CourseTeacher;
import com.orange.edu.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "师资管理接口", tags = "师资管理接口")
@RestController
public class CourseTeacherController {
    @Autowired
    private CourseTeacherService courseTeacherService;

    /**
     * 查询教师接口
     *
     * @param id id
     * @return {@link List}<{@link CourseTeacher}>
     */
    @ApiOperation("查询教师接口")
    @GetMapping("/courseTeacher/list/{id}")
    public List<CourseTeacher> queryCourseTeacherList(@PathVariable Long id){
        return courseTeacherService.queryCourseTeacherList(id);
    }

    /**
     * 添加与修改教师接口
     *
     * @param courseTeacher 课程老师
     * @return {@link CourseTeacher}
     */
    @ApiOperation("添加与修改教师接口")
    @PostMapping("/courseTeacher")
    public CourseTeacher saveCourseTeacher(@RequestBody @Validated(ValidationGroups.Insert.class) CourseTeacher courseTeacher){
        return courseTeacherService.saveCourseTeacher(courseTeacher);
    }

    /**
     * 删除教师接口
     *
     * @param courseId  课程id
     * @param teacherId 教师id
     */
    @ApiOperation("删除教师接口")
    @DeleteMapping("/courseTeacher/course/{courseId}/{teacherId}")
    public void deleteCourseTeacher(@PathVariable  Long courseId,@PathVariable Long teacherId){
        courseTeacherService.deleteCourseTeacher(courseId,teacherId);
    }

}
