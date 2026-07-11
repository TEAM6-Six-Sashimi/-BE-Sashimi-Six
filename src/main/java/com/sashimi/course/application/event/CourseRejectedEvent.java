package com.sashimi.course.application.event;

import com.sashimi.course.domain.model.RejectReasonCategory;

public record CourseRejectedEvent(
        Long instructorId,
        String instructorEmail,
        String instructorName,
        Long courseId,
        String courseTitle,
        RejectReasonCategory category,
        String detail
) {
}
