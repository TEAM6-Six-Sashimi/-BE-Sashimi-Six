package com.sashimi.credit.application.event;

public record CreditChargedEvent(
        Long userId,
        String email,
        String name,
        Long amount,
        Long balanceAfter
) {
}
