package com.sashimi.subscription.application.usecase;

import java.time.LocalDateTime;

public interface SubscriptionCommandUseCase {

    CancelResult cancel(Long userId);

    record CancelResult(
            Long subscriptionId,
            String planCode,
            String status,
            boolean autoRenew,
            LocalDateTime effectiveUntil
    ) {
    }
}