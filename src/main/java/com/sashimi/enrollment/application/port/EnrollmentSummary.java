package com.sashimi.enrollment.application.port;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EnrollmentSummary(
        Long courseId,
        BigDecimal progressRate,
        boolean completed,
        LocalDateTime enrolledAt
) {}