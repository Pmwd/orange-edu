package com.orange.edu.learning.model.dto;

import com.orange.edu.learning.model.po.CourseTables;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 我的课程查询条件
 *
 * @author Wuxy
 * @version 1.0
 * @since 2022/10/6 9:42
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class MyCourseTableItemDto extends CourseTables {

    /**
     * 最近学习时间
     */
    private LocalDateTime learnDate;

    /**
     * 学习时长
     */
    private Long learnLength;

    /**
     * 章节id
     */
    private Long teachplanId;

    /**
     * 章节名称
     */
    private String teachplanName;

}
