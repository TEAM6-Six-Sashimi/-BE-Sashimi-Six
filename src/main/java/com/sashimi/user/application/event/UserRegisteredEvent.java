package com.sashimi.user.application.event;

public record UserRegisteredEvent(
        Long userId,
        String name,
        String email
) {
}
