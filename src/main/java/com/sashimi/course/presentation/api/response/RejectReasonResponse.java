package com.sashimi.course.presentation.api.response;

import com.sashimi.course.domain.model.RejectReasonCategory;

public record RejectReasonResponse(
        String code,
        String label
) {
    public static RejectReasonResponse from(RejectReasonCategory category) {
        return new RejectReasonResponse(category.name(), category.getLabel());
    }
}
