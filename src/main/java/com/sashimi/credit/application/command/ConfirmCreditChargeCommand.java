package com.sashimi.credit.application.command;

public record ConfirmCreditChargeCommand(
        Long userId,
        String paymentKey,
        String orderId,
        Long amount
) {
}