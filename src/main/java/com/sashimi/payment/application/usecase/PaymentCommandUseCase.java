package com.sashimi.payment.application.usecase;

import com.sashimi.payment.application.command.PaymentCheckoutCommand;
import com.sashimi.payment.application.command.PaymentPurchaseType;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentCommandUseCase {

    PaymentResult checkout(
            PaymentCheckoutCommand command
    );

    record PaymentResult(
            Long orderId,
            String orderNo,
            Long paymentId,
            PaymentPurchaseType purchaseType,
            Long amount,
            String status,
            Long creditBalance,
            List<PaidCourse> courses,
            PaidSubscription subscription
    ) {
    }

    record PaidCourse(
            Long courseId,
            String title,
            Long price
    ) {
    }

    record PaidSubscription(
            Long subscriptionId,
            String planCode,
            String planName,
            String status,
            LocalDateTime startedAt,
            LocalDateTime expiresAt,
            LocalDateTime nextBillingAt
    ) {
    }
}