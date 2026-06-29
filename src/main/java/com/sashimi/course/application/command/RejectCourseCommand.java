package com.sashimi.course.application.command;

import com.sashimi.course.domain.model.RejectReasonCategory;

public record RejectCourseCommand(
        Long courseId,
        RejectReasonCategory category,
        String detail
) {}
