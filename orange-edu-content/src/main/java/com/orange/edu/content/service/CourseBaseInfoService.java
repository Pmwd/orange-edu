package com.orange.edu.content.service;

import com.orange.base.model.PageParams;
import com.orange.base.model.PageResult;
import com.orange.base.model.dto.QueryCourseParamsDto;
import com.orange.edu.content.model.po.CourseBase;

/**
 * @description 课程基本信息管理业务接口
 **/
public interface CourseBaseInfoService  {

/*
 * @description 课程查询接口
 * @param pageParams 分页参数
 * @param queryCourseParamsDto 条件条件
 */
  PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

}

