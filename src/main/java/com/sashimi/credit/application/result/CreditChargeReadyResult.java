package com.sashimi.credit.application.result;

public record CreditChargeReadyResult(
        String orderId,
        String orderName,
        Long amount
) {
}