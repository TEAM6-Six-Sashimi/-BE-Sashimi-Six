package com.sashimi.course.application.usecase;

import com.sashimi.course.application.query.PublicCourseDetailView;
import com.sashimi.course.application.query.PublicCourseView;

import java.util.List;

public interface PublicCourseQueryUseCase {
    List<PublicCourseView> getAllApprovedCourses();
    List<PublicCourseView> getCoursesByCategory(String categoryName);
    List<PublicCourseView> getCoursesBySubCategory(Long categoryId);
    PublicCourseDetailView getCourseDetail(Long courseId, Long userId, boolean isAdmin);
}