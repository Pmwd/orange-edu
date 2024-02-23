package com.orange.edu.content.service;

import com.orange.edu.content.model.po.CourseTeacher;

import java.util.List;

public interface CourseTeacherService {
    List<CourseTeacher> queryCourseTeacherList(Long id);

    CourseTeacher saveCourseTeacher(CourseTeacher courseTeacher);

    void deleteCourseTeacher(Long courseId, Long teacherId);
}
