package com.sashimi.user.application.event;

public record SuspiciousLoginDetectedEvent(
        Long userId,
        String name,
        String email,
        int violationCount,
        long lockDurationSeconds
) {
}
