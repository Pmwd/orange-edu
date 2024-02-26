package com.orange.edu.learning.controller;


import com.orange.base.exception.OrangeEduException;
import com.orange.base.model.PageResult;
import com.orange.edu.content.util.SecurityUtil;
import com.orange.edu.learning.model.dto.ChooseCourseDto;
import com.orange.edu.learning.model.dto.CourseTablesDto;
import com.orange.edu.learning.model.dto.MyCourseTableParams;
import com.orange.edu.learning.model.po.CourseTables;
import com.orange.edu.learning.service.MyCourseTablesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 我的课程表接口
 *
 *
 */
@Api(value = "我的课程表接口", tags = "我的课程表接口")
@Slf4j
@RestController
public class MyCourseTableController {

    @Autowired
    private MyCourseTablesService myCourseTablesService;


    @ApiOperation("添加选课")
    @PostMapping("/choosecourse/{courseId}")
    public ChooseCourseDto addChooseCourse(@PathVariable("courseId") Long courseId) {
        // 当前登录用户
        SecurityUtil.OeUser user = SecurityUtil.getUser();
        if (user == null) {
            OrangeEduException.cast("请登陆后继续选课");
        }
        String userId = user.getId();
        return myCourseTablesService.addChooseCourse(userId, courseId);
    }

    @ApiOperation("查询学习资格")
    @PostMapping("/choosecourse/learnstatus/{courseId}")
    public CourseTablesDto getLearnStatus(@PathVariable("courseId") Long courseId) {
        SecurityUtil.OeUser user = SecurityUtil.getUser();
        if (user == null) {
            OrangeEduException.cast("请登陆后查询学习资格");
        }
        return myCourseTablesService.getLearningStatus(user.getId(), courseId);
    }

    @ApiOperation("我的课程表")
    @GetMapping("/mycoursetable")
    public PageResult<CourseTables> mycoursetable(MyCourseTableParams params) {
        //登录用户
        SecurityUtil.OeUser user = SecurityUtil.getUser();
        if(user == null){
            OrangeEduException.cast("请登录后继续选课");
        }
        String userId = user.getId();
        //设置当前的登录用户
        params.setUserId(userId);

        return myCourseTablesService.mycourestabls(params);
    }

}
