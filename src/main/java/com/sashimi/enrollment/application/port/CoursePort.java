package com.sashimi.enrollment.application.port;

public interface CoursePort {
    EnrolledCourseInfo getCourseInfo(Long courseId);
    CourseDetailInfo getCourseDetailInfo(Long courseId);
}