package com.orange.edu.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.orange.base.exception.CommonError;
import com.orange.base.exception.OrangeEduException;
import com.orange.base.model.PageResult;
import com.orange.edu.content.model.po.CoursePublish;
import com.orange.edu.learning.feignclient.ContentServiceClient;
import com.orange.edu.learning.mapper.ChooseCourseMapper;
import com.orange.edu.learning.mapper.CourseTablesMapper;
import com.orange.edu.learning.model.dto.ChooseCourseDto;
import com.orange.edu.learning.model.dto.CourseTablesDto;
import com.orange.edu.learning.model.dto.MyCourseTableParams;
import com.orange.edu.learning.model.po.ChooseCourse;
import com.orange.edu.learning.model.po.CourseTables;
import com.orange.edu.learning.service.MyCourseTablesService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 */
@Service
public class MyCourseTableServicesImpl implements MyCourseTablesService {

    @Resource
    private ChooseCourseMapper chooseCourseMapper;

    @Resource
    private CourseTablesMapper courseTablesMapper;

    @Autowired
    private ContentServiceClient contentServiceClient;

    @Autowired
    private MyCourseTablesService currentPoxy;


    @Override
    public ChooseCourseDto addChooseCourse(String userId, Long courseId) {
        // 查询课程信息
        CoursePublish coursePublish = contentServiceClient.getCoursePublish(courseId);
        if (coursePublish == null) {
            OrangeEduException.cast("课程信息不存在");
        }
        Long id = coursePublish.getId();
        if (id == null) {
            OrangeEduException.cast(CommonError.UNKNOWN_ERROR);
        }
        // 课程收费标准
        String charge = coursePublish.getCharge();
        ChooseCourse chooseCourse;
        if ("201000".equals(charge)) {
            // 添加免费课程到选课记录表 + 添加到我的课程表
            chooseCourse = currentPoxy.addFreeCourse(userId, coursePublish);
        } else {
            // 添加收费课程，只能添加到选课记录表
            chooseCourse = currentPoxy.addChargeCourse(userId, coursePublish);
        }

        ChooseCourseDto chooseCourseDto = new ChooseCourseDto();
        BeanUtils.copyProperties(chooseCourse, chooseCourseDto);
        // 获取用户对该课程的学习资格
        CourseTablesDto courseTablesDto = getLearningStatus(userId, courseId);
        chooseCourseDto.setLearnStatus(courseTablesDto.getLearnStatus());
        return chooseCourseDto;
    }

    @Transactional
    @Override
    public ChooseCourse addFreeCourse(String userId, CoursePublish coursePublish) {
        // 查询选课记录表是否已经存在免费的且选课成功的订单
        LambdaQueryWrapper<ChooseCourse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChooseCourse::getUserId, userId)
                .eq(ChooseCourse::getCourseId, coursePublish.getId())
                .eq(ChooseCourse::getOrderType, "700001") // 免费课程
                .eq(ChooseCourse::getStatus, "701001"); // 选课成功
        List<ChooseCourse> chooseCourses = chooseCourseMapper.selectList(queryWrapper);
        // 已经存在免费的且选课成功的订单直接返回
        if (chooseCourses != null && chooseCourses.size() > 0) {
            return chooseCourses.get(0);
        }
        // 添加选课记录信息
        ChooseCourse chooseCourse = new ChooseCourse();
        chooseCourse.setCourseId(coursePublish.getId());
        chooseCourse.setCourseName(coursePublish.getName());
        chooseCourse.setCoursePrice(0f); // 免费课程价格为0
        chooseCourse.setUserId(userId);
        chooseCourse.setCompanyId(coursePublish.getCompanyId());
        chooseCourse.setOrderType("700001"); // 免费课程
        chooseCourse.setCreateDate(LocalDateTime.now());
        chooseCourse.setStatus("701001"); // 选课成功
        chooseCourse.setValidDays(365); // 免费课程默认为 365
        chooseCourse.setValidtimeStart(LocalDateTime.now());
        chooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(365));

        // 向选课记录表中添加记录
        chooseCourseMapper.insert(chooseCourse);
        // 添加到我的课程表
        CourseTables courseTables = addCourseTables(chooseCourse);

        return chooseCourse;
    }

    @Transactional
    @Override
    public ChooseCourse addChargeCourse(String userId, CoursePublish coursePublish) {
        // 如果存在待支付交易记录直接返回
        List<ChooseCourse> chooseCourses = chooseCourseMapper.selectList(new LambdaQueryWrapper<ChooseCourse>()
                .eq(ChooseCourse::getUserId, userId)
                .eq(ChooseCourse::getCourseId, coursePublish.getId())
                .eq(ChooseCourse::getOrderType, "700002") // 收费订单
                .eq(ChooseCourse::getStatus, "701002") // 待支付
        );
        if (chooseCourses != null && chooseCourses.size() > 0) {
            return chooseCourses.get(0);
        }
        // 创建选课记录
        ChooseCourse chooseCourse = new ChooseCourse();
        chooseCourse.setCourseId(coursePublish.getId());
        chooseCourse.setCourseName(coursePublish.getName());
        chooseCourse.setCoursePrice(coursePublish.getPrice());
        chooseCourse.setUserId(userId);
        chooseCourse.setCompanyId(coursePublish.getCompanyId());
        chooseCourse.setOrderType("700002"); // 收费课程
        chooseCourse.setCreateDate(LocalDateTime.now());
        chooseCourse.setStatus("701002"); // 待支付
        chooseCourse.setValidDays(coursePublish.getValidDays());
        chooseCourse.setValidtimeStart(LocalDateTime.now());
        chooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(coursePublish.getValidDays()));

        // 插入一条选课记录到数据库中
        chooseCourseMapper.insert(chooseCourse);

        return chooseCourse;
    }

    @Transactional
    @Override
    public CourseTables addCourseTables(ChooseCourse chooseCourse) {
        // 选课记录完成且尚未过期可以添加到课程表
        String status = chooseCourse.getStatus();
        if (!"701001".equals(status)) {
            OrangeEduException.cast("选课未成功，无法添加到课程表");
        }
        // 查询我的课程表
        CourseTables courseTables = getCourseTables(chooseCourse.getUserId(), chooseCourse.getCourseId());
        if (courseTables != null) {
            return courseTables;
        }
        // 新增课程表
        courseTables = new CourseTables();
        courseTables.setChooseCourseId(chooseCourse.getId());
        courseTables.setUserId(chooseCourse.getUserId());
        courseTables.setCourseId(chooseCourse.getCourseId());
        courseTables.setCompanyId(chooseCourse.getCompanyId());
        courseTables.setCourseName(chooseCourse.getCourseName());
        courseTables.setCreateDate(LocalDateTime.now());
        courseTables.setValidtimeStart(chooseCourse.getValidtimeStart());
        courseTables.setValidtimeEnd(chooseCourse.getValidtimeEnd());
        courseTables.setCourseType(chooseCourse.getOrderType());
        // 添加到数据库
        courseTablesMapper.insert(courseTables);

        return courseTables;
    }

    @Override
    public CourseTables getCourseTables(String userId, Long courseId) {
        return courseTablesMapper.selectOne(new LambdaQueryWrapper<CourseTables>()
                .eq(CourseTables::getUserId, userId)
                .eq(CourseTables::getCourseId, courseId));
    }

    @Override
    public CourseTablesDto getLearningStatus(String userId, Long courseId) {
        // 查询我的课程表
        CourseTables courseTables = getCourseTables(userId, courseId);
        if (courseTables == null) {
            CourseTablesDto courseTablesDto = new CourseTablesDto();
            // 没有选课或选课后没有支付
            courseTablesDto.setLearnStatus("702002");
            return courseTablesDto;
        }
        CourseTablesDto courseTablesDto = new CourseTablesDto();
        BeanUtils.copyProperties(courseTables, courseTablesDto);
        // 是否过期
        boolean isExpires = courseTables.getValidtimeEnd().isBefore(LocalDateTime.now());
        if (!isExpires) {
            // 未过期，正常学习
            courseTablesDto.setLearnStatus("702001");
        } else {
            // 已过期
            courseTablesDto.setLearnStatus("702003");
        }
        return courseTablesDto;
    }

    @Override
    public boolean saveChooseCourseStatus(String chooseCourseId) {
        return false;
    }


    @Override
    public PageResult<CourseTables> mycourestabls( MyCourseTableParams params){
        //页码
        long pageNo = params.getPage();
        //每页记录数,固定为4
        long pageSize = 4;
        //分页条件
        Page<CourseTables> page = new Page<>(pageNo, pageSize);
        //根据用户id查询
        String userId = params.getUserId();
        LambdaQueryWrapper<CourseTables> lambdaQueryWrapper = new LambdaQueryWrapper<CourseTables>().eq(CourseTables::getUserId, userId);

        //分页查询
        Page<CourseTables> pageResult = courseTablesMapper.selectPage(page, lambdaQueryWrapper);
        List<CourseTables> records = pageResult.getRecords();
        //记录总数
        long total = pageResult.getTotal();
        PageResult<CourseTables> courseTablesResult = new PageResult<>(records, total, pageNo, pageSize);
        return courseTablesResult;
    }

}

