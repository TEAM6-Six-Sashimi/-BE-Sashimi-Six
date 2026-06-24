package com.sashimi.instructorapplication.application.event;

public record InstructorApprovedEvent(
        Long userId,
        String name,
        String email
) {
}
