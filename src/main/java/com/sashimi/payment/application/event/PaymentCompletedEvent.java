package com.sashimi.payment.application.event;

import com.sashimi.payment.application.usecase.PaymentCommandUseCase.PaymentResult;

public record PaymentCompletedEvent(
        Long userId,
        String email,
        String name,
        PaymentResult result
) {
}
