package com.sashimi.member.application.event;

public record InstructorApprovedEvent(
        Long userId,
        String name,
        String email
) {
}
