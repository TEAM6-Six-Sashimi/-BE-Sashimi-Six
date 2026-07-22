package com.sashimi.instructorapplication.application.event;

public record InstructorAppliedEvent(
        Long userId,
        String name,
        String email
) {
}
