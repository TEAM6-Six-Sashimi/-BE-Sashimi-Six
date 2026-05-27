package com.sashimi.course.application.query;

import java.math.BigDecimal;

public record PublicCourseView(
        String instructorName,
        String title,
        BigDecimal price,
        String thumbnail,
        int totalDuration,
        BigDecimal ratingAvg,
        int studentCount
) {}