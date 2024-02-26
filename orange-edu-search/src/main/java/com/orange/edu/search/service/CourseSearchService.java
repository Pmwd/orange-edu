package com.orange.edu.search.service;

import com.orange.base.model.PageParams;
import com.orange.edu.search.model.dto.SearchCourseParamDto;
import com.orange.edu.search.model.dto.SearchPageResultDto;
import com.orange.edu.search.model.po.CourseIndex;

public interface CourseSearchService {


    /**
     * @description 搜索课程列表
     * @param pageParams 分页参数
     * @param searchCourseParamDto 搜索条件
     *
     */
    SearchPageResultDto<CourseIndex> queryCoursePubIndex(PageParams pageParams, SearchCourseParamDto searchCourseParamDto);

}

