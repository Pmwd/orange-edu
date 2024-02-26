package com.orange.edu.content.controller;


import com.orange.base.model.PageParams;
import com.orange.base.model.PageResult;
import com.orange.base.model.dto.QueryCourseParamsDto;
import com.orange.edu.content.model.dto.AddCourseDto;
import com.orange.edu.content.model.dto.CourseBaseInfoDto;
import com.orange.edu.content.model.dto.EditCourseDto;
import com.orange.edu.content.model.po.CourseBase;
import com.orange.edu.content.service.CourseBaseInfoService;
import com.orange.edu.content.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(value = "课程信息编辑接口",tags = "课程信息编辑接口")
@RestController
public class CourseBaseInfoController {

    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @ApiOperation("课程查询接口")
    @PostMapping("/list")
    @PreAuthorize("hasAuthority('xc_teachmanager_course_list')") // 拥有课程列表查询的授权方可访问（jwt中保存了UserDetails信息）
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody QueryCourseParamsDto queryCourseParams) {
        // 取出身份
        SecurityUtil.OeUser user = SecurityUtil.getUser();
        // 得到机构id
        assert user != null;
        Long companyId = user.getCompanyId();
        // 调用 service 获取数据 （实现细粒度授权，本机构只能查询自己机构的课程列表）
        return courseBaseInfoService.queryCourseBaseList(companyId, pageParams, queryCourseParams);
    }

    @ApiOperation("新增课程基础信息")
    @PostMapping("/course")
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated AddCourseDto addCourseDto){
        //机构id，由于认证系统没有上线暂时硬编码
        Long companyId = 1232141425L;
        return courseBaseInfoService.createCourseBase(companyId,addCourseDto);

    }

    @ApiOperation("修改课程基础信息")
    @PutMapping("/course")
    public CourseBaseInfoDto modifyCourseBase(@RequestBody @Validated EditCourseDto editCourseDto){
        //机构id，由于认证系统没有上线暂时硬编码
        Long companyId = 1232141425L;
        return courseBaseInfoService.updateCourseBase(companyId,editCourseDto);
    }

    @ApiOperation("根据课程id查询课程基础信息")
    @GetMapping("/course/{courseId}")
    public CourseBaseInfoDto getCourseBaseById(@PathVariable Long courseId){
        return courseBaseInfoService.getCourseBaseInfo(courseId);
    }

    /**
     * 删除课程
     *
     * @param courseId 课程id
     */
    @ApiOperation("删除课程")
    @DeleteMapping("/course/{courseId}")
    public void deleteCourseBase(@PathVariable Long courseId){
        Long companyId = 1232141425L;
        courseBaseInfoService.deleteCourseBase(companyId,courseId);
    }

}
