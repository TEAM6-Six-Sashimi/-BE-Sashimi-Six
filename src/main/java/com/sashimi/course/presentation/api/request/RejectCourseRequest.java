package com.sashimi.course.presentation.api.request;

import com.sashimi.course.domain.model.RejectReasonCategory;

public record RejectCourseRequest(
        RejectReasonCategory category,
        String detail
) {}
