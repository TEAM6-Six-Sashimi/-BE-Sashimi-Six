package com.sashimi.course.presentation.api.response;

import com.sashimi.course.application.query.PublicCourseView;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PublicCourseResponse(
        Long courseId,
        String instructorName,
        String title,
        Long price,
        String thumbnail,
        int totalDuration,
        BigDecimal ratingAvg,
        int studentCount,
        LocalDateTime approvedAt,
        String label
) {
    public static PublicCourseResponse from(PublicCourseView view) {
        return new PublicCourseResponse(
                view.courseId(),
                view.instructorName(),
                view.title(),
                view.price(),
                view.thumbnail(),
                view.totalDuration(),
                view.ratingAvg(),
                view.studentCount(),
                view.approvedAt(),
                view.label()
        );
    }
}