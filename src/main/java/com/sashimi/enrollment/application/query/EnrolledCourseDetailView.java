package com.sashimi.enrollment.application.query;

import com.sashimi.enrollment.application.port.CourseDetailInfo;

import java.math.BigDecimal;

public record EnrolledCourseDetailView(
        CourseDetailInfo course,
        BigDecimal progressRate,
        boolean completed
) {}