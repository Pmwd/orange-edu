package com.orange.edu.search.controller;

import com.orange.base.model.PageParams;
import com.orange.edu.search.model.dto.SearchCourseParamDto;
import com.orange.edu.search.model.dto.SearchPageResultDto;
import com.orange.edu.search.model.po.CourseIndex;
import com.orange.edu.search.service.CourseSearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 课程搜索接口
 *
 *
 */
@Api(value = "课程搜索接口", tags = "课程搜索接口")
@RestController
@RequestMapping("/course")
public class CourseSearchController {
    @Autowired
    private CourseSearchService courseSearchService;

    @ApiOperation("课程搜索列表")
    @GetMapping("/list")
    public SearchPageResultDto<CourseIndex> list(PageParams pageParams, SearchCourseParamDto searchCourseParamDto) {
        return courseSearchService.queryCoursePubIndex(pageParams, searchCourseParamDto);
    }
}
