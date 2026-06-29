package com.sashimi.course.presentation.api.response;

import com.sashimi.course.domain.model.Course;

import java.time.LocalDateTime;

public record AdminCourseRejectedResponse(
        Long courseId,
        String title,
        String instructorName,
        String categoryName,
        LocalDateTime updatedAt,
        RejectReasonResponse rejectCategory,
        String rejectDetail
) {
    public static AdminCourseRejectedResponse of(Course course, String categoryName, String instructorName) {
        RejectReasonResponse rejectCategory = course.getRejectReasonCategory() == null
                ? null
                : RejectReasonResponse.from(course.getRejectReasonCategory());
        return new AdminCourseRejectedResponse(
                course.getId(),
                course.getTitle(),
                instructorName,
                categoryName,
                course.getUpdatedAt(),
                rejectCategory,
                course.getRejectDetail()
        );
    }
}