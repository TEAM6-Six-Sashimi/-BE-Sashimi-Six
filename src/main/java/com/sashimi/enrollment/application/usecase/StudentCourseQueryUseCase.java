package com.sashimi.enrollment.application.usecase;

import com.sashimi.enrollment.application.query.EnrolledCourseDetailView;
import com.sashimi.enrollment.application.query.EnrolledCourseView;

import java.util.List;

public interface StudentCourseQueryUseCase {
    List<EnrolledCourseView> getEnrolledCourses(Long userId);
    EnrolledCourseDetailView getEnrolledCourseDetail(Long userId, Long courseId);
}