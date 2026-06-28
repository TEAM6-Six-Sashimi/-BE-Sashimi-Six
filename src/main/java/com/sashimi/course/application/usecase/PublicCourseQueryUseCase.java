package com.sashimi.course.application.usecase;

import com.sashimi.course.application.query.PublicCourseDetailView;
import com.sashimi.course.application.query.PublicCourseView;
import com.sashimi.course.application.query.RejectReasonView;

import java.util.List;

public interface PublicCourseQueryUseCase {
    List<PublicCourseView> getAllApprovedCourses();
    List<PublicCourseView> getCoursesByCategory(String categoryName);
    List<PublicCourseView> getCoursesBySubCategory(Long categoryId);
    List<PublicCourseView> getCoursesByIds(List<Long> ids);
    PublicCourseDetailView getCourseDetail(Long courseId, Long userId, boolean isAdmin);
    RejectReasonView getRejectReason(Long courseId, Long userId, boolean isAdmin);
}