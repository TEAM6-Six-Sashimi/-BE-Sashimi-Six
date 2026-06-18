package com.sashimi.credit.infrastructure.toss;

import java.time.OffsetDateTime;

public record TossPaymentConfirmResponse(
        String paymentKey,
        String orderId,
        String orderName,
        String status,
        Long totalAmount,
        OffsetDateTime approvedAt,
        String method
) {
}