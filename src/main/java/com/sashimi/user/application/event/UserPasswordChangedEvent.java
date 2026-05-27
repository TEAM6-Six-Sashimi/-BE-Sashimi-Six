package com.sashimi.user.application.event;

import java.time.LocalDateTime;

public record UserPasswordChangedEvent(
        Long userId,
        String email,
        LocalDateTime changedAt
) {
}
