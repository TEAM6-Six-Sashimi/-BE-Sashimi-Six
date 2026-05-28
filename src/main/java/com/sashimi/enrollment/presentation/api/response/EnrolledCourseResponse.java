package com.sashimi.enrollment.presentation.api.response;

import com.sashimi.enrollment.application.query.EnrolledCourseView;

import java.math.BigDecimal;

public record EnrolledCourseResponse(
        String title,
        String thumbnail,
        String instructorName,
        BigDecimal progressRate,
        boolean completed
) {
    public static EnrolledCourseResponse from(EnrolledCourseView view) {
        return new EnrolledCourseResponse(
                view.title(),
                view.thumbnail(),
                view.instructorName(),
                view.progressRate(),
                view.completed()
        );
    }
}