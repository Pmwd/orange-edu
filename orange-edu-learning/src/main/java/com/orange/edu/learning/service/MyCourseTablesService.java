package com.orange.edu.learning.service;

import com.orange.base.model.PageResult;
import com.orange.edu.content.model.po.CoursePublish;
import com.orange.edu.learning.model.dto.ChooseCourseDto;
import com.orange.edu.learning.model.dto.CourseTablesDto;
import com.orange.edu.learning.model.dto.MyCourseTableParams;
import com.orange.edu.learning.model.po.ChooseCourse;
import com.orange.edu.learning.model.po.CourseTables;


/**
 * 我的课程表service接口
 *
 * @author Wuxy
 * @version 1.0
 * @since 2022/10/2 16:07
 */
public interface MyCourseTablesService {

    /**
     * 添加选课
     *
     * @param userId   用户 id
     * @param courseId 课程 id
     *
     */
    ChooseCourseDto addChooseCourse(String userId, Long courseId);

    /**
     * 添加免费课程
     *
     * @param userId        用户 id
     * @param coursePublish 课程发布信息
     * @return 选课信息
     */
    ChooseCourse addFreeCourse(String userId, CoursePublish coursePublish);

    /**
     * 添加收费课程
     *
     * @param userId        用户 id
     * @param coursePublish 课程发布信息
     * @return 选课信息
     */
    ChooseCourse addChargeCourse(String userId, CoursePublish coursePublish);

    /**
     * 添加到我的课程表
     *
     * @param chooseCourse 选课记录
     *
     */
    CourseTables addCourseTables(ChooseCourse chooseCourse);

    /**
     * 根据课程和用户查询我的课程表中某一门课程
     *
     * @param userId   用户 id
     * @param courseId 课程 id
     *
     */
    CourseTables getCourseTables(String userId, Long courseId);

    /**
     * 判断学习资格
     * <pre>
     * 学习资格状态 [{"code":"702001","desc":"正常学习"},
     *            {"code":"702002","desc":"没有选课或选课后没有支付"},
     *            {"code":"702003","desc":"已过期需要申请续期或重新支付"}]
     * </pre>
     *
     * @param userId   用户 id
     * @param courseId 课程 id
     *
     */
    CourseTablesDto getLearningStatus(String userId, Long courseId);

    boolean saveChooseCourseStatus(String chooseCourseId);

    /**
     * @description 我的课程表
     * @param params
     *
     */
    public PageResult<CourseTables> mycourestabls(MyCourseTableParams params);


}
