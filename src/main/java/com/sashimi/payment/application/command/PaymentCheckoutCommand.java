package com.sashimi.payment.application.command;

import com.sashimi.subscription.domain.model.SubscriptionPlan;

public record PaymentCheckoutCommand(
        Long userId,
        PaymentPurchaseType purchaseType,
        Long courseId,
        SubscriptionPlan planCode,
        Boolean agreed,
        String idempotencyKey
) {
}