package com.sashimi.credit.infrastructure.toss;

public record TossPaymentConfirmResponse(
        String paymentKey,
        String orderId,
        String orderName,
        String status,
        Long totalAmount,
        String approvedAt,
        String method
) {
}