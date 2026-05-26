package com.sashimi.user.application.event;

import java.time.LocalDateTime;

public record UserWithdrawnEvent(
        Long userId,
        LocalDateTime withdrawnAt
) {
}