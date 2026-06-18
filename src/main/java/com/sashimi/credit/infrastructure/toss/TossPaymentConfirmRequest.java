package com.sashimi.credit.infrastructure.toss;

public record TossPaymentConfirmRequest(
        String paymentKey,
        String orderId,
        Long amount
) {
}