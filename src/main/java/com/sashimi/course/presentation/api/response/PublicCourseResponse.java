package com.sashimi.course.presentation.api.response;

import com.sashimi.course.application.query.PublicCourseView;

import java.math.BigDecimal;

public record PublicCourseResponse(
        String instructorName,
        String title,
        BigDecimal price,
        String thumbnail,
        int totalDuration,
        BigDecimal ratingAvg,
        int studentCount
) {
    public static PublicCourseResponse from(PublicCourseView view) {
        return new PublicCourseResponse(
                view.instructorName(),
                view.title(),
                view.price(),
                view.thumbnail(),
                view.totalDuration(),
                view.ratingAvg(),
                view.studentCount()
        );
    }
}