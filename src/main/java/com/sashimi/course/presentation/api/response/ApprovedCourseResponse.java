package com.sashimi.course.presentation.api.response;

import com.sashimi.course.domain.model.Course;

import java.math.BigDecimal;

public record ApprovedCourseResponse(
        Long categoryId,
        String title,
        BigDecimal price,
        BigDecimal ratingAvg,
        int studentCount
) {
    public static ApprovedCourseResponse from(Course course) {
        return new ApprovedCourseResponse(
                course.getCategoryId(),
                course.getTitle(),
                course.getPrice(),
                course.getRatingAvg(),
                course.getStudentCount()
        );
    }
}