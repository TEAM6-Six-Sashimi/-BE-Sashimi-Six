package com.sashimi.credit.application.result;

public record CreditChargeConfirmResult(
        Long balance,
        String orderId,
        String paymentKey,
        Long chargedAmount
) {
}