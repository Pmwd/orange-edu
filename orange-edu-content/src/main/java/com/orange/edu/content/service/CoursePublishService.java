package com.orange.edu.content.service;

import com.orange.edu.content.model.dto.CoursePreviewDto;
import com.orange.edu.content.model.po.CoursePublish;

import java.io.File;

/**
 * 课程预览、发布接口
 *
 *
 */
public interface CoursePublishService {

    /**
     * 获取课程预览信息
     *
     * @param courseId 课程 id
     *
     */
    CoursePreviewDto getCoursePreviewInfo(Long courseId);

    /**
     * 通过 /open 接口获取课程预览信息
     *
     *
     */
    CoursePreviewDto getOpenCoursePreviewInfo(Long courseId);

    /**
     * 提交审核
     *
     * @param courseId 课程 id
     *
     */
    void commitAudit(Long companyId, Long courseId);

    /**
     * 课程发布接口
     *
     * @param companyId 机构 id
     * @param courseId  课程 id
     *
     */
    void publish(Long companyId, Long courseId);

    /**
     * 课程静态化
     *
     * @param courseId 课程 id
     * @return {@link File} 静态化文件
     *
     */
    File generateCourseHtml(Long courseId);

    /**
     * 上传课程静态化页面
     *
     * @param courseId 课程 id
     * @param file     静态化文件
     *
     */
    void uploadCourseHtml(Long courseId, File file);

    /**
     * 新增课程索引
     *
     * @param courseId 课程id
     * @return 新增成功返回 true，否则 false
     */
    Boolean saveCourseIndex(Long courseId);

    /**
     * 根据课程id查询课程发布信息
     *
     * @param courseId 课程id
     * @return 课程发布信息
     */
    CoursePublish getCoursePublish(Long courseId);

    /**
     * 根据课程 id 查询缓存（Redis等）中的课程发布信息
     * <ol>
     *     <li>基于缓存空值解决缓存穿透问题</li>
     *     <li>基于redisson分布式锁解决缓存击穿问题</li>
     * </ol>
     *
     * @param courseId 课程id
     * @return 课程发布信息
     */
    CoursePublish getCoursePublishCache(Long courseId);

}
