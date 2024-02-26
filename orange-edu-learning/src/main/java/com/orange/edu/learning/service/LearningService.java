package com.orange.edu.learning.service;


import com.orange.base.model.RestResponse;

/**
 * 学习课程管理 service 接口
 *
 * @author Wuxy
 * @version 1.0
 * @since 2022/10/2 16:07
 */
public interface LearningService {

    /**
     * 获取教学视频
     *
     * @param courseId    课程id
     * @param teachplanId 课程计划id
     * @param mediaId     视频文件id
     *
     */
    RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId);

}
