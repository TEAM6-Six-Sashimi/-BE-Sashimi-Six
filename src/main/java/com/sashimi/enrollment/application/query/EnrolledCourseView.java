package com.sashimi.enrollment.application.query;

import java.math.BigDecimal;

public record EnrolledCourseView(
        Long courseId,
        String title,
        String thumbnail,
        String instructorName,
        BigDecimal progressRate,
        boolean completed
) {}