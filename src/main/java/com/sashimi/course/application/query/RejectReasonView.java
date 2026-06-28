package com.sashimi.course.application.query;

import com.sashimi.course.domain.model.RejectReasonCategory;

import java.time.LocalDateTime;

public record RejectReasonView(
        Long courseId,
        String title,
        LocalDateTime rejectedAt,
        RejectReasonCategory category,
        String detail
) {}
