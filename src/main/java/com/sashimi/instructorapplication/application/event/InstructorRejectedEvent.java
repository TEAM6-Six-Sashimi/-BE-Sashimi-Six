package com.sashimi.instructorapplication.application.event;

import com.sashimi.instructorapplication.domain.model.RejectionCategory;

public record InstructorRejectedEvent(
        Long userId,
        String name,
        String email,
        RejectionCategory rejectionCategory,
        String rejectionReason
) {
}
