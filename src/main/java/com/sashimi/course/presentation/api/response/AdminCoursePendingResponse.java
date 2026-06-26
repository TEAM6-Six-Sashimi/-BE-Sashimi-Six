package com.sashimi.course.presentation.api.response;

import com.sashimi.course.domain.model.Course;

import java.time.LocalDateTime;

public record AdminCoursePendingResponse(
        Long courseId,
        String instructorLoginId,
        String title,
        String instructorName,
        String categoryName,
        LocalDateTime createdAt
) {
    public static AdminCoursePendingResponse of(Course course, String categoryName, String instructorName, String instructorLoginId) {
        return new AdminCoursePendingResponse(
                course.getId(),
                instructorLoginId,
                course.getTitle(),
                instructorName,
                categoryName,
                course.getCreatedAt()
        );
    }
}