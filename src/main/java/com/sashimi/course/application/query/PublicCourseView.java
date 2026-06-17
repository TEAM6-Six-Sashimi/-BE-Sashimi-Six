package com.sashimi.course.application.query;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PublicCourseView(
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
) {}