package com.sashimi.payment.application.command;

public record PaymentCheckoutCommand(

        Long userId,
        PaymentPurchaseType purchaseType,
        Long courseId,
        Boolean agreed

) {
}
