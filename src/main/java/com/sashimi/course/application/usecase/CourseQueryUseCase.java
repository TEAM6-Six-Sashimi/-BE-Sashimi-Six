package com.sashimi.course.application.usecase;

import com.sashimi.course.domain.model.Course;

import java.util.List;

public interface CourseQueryUseCase {
    List<Course> getApprovedCoursesByInstructor(Long instructorId);
    List<Course> getInProgressCoursesByInstructor(Long instructorId);
    Course getCourseDetail(Long courseId, Long instructorId);
    List<Course> getApprovedCoursesForAdmin();
    List<Course> getPendingCoursesForAdmin();
    List<Course> getRejectedCoursesForAdmin();
}
