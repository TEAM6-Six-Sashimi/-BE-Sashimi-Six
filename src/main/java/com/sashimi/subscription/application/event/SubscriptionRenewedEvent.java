package com.sashimi.subscription.application.event;

import java.time.LocalDateTime;

public record SubscriptionRenewedEvent(
        Long userId,
        String email,
        String name,
        String planName,
        LocalDateTime nextBillingAt
) {
}
