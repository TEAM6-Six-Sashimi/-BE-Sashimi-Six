package com.sashimi.course.presentation.api.response;

import com.sashimi.course.domain.model.Course;

import java.time.LocalDateTime;

public record AdminCourseRejectedResponse(
        Long courseId,
        String title,
        String instructorName,
        String categoryName,
        LocalDateTime updatedAt,
        String rejectReason
) {
    public static AdminCourseRejectedResponse of(Course course, String categoryName, String instructorName) {
        return new AdminCourseRejectedResponse(
                course.getId(),
                course.getTitle(),
                instructorName,
                categoryName,
                course.getUpdatedAt(),
                course.getRejectReason()
        );
    }
}