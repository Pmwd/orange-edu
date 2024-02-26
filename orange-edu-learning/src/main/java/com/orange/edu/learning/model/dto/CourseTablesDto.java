package com.orange.edu.learning.model.dto;

import com.orange.edu.learning.model.po.CourseTables;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 我的课程表模型类
 *
 * @author Wuxy
 * @version 1.0
 * @since 2022/10/2 16:09
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class CourseTablesDto extends CourseTables {

    /**
     * 学习资格，[{"code":"702001","desc":"正常学习"},{"code":"702002","desc":"没有选课或选课后没有支付"},{"code":"702003","desc":"已过期需要申请续期或重新支付"}]
     */
    public String learnStatus;
}
