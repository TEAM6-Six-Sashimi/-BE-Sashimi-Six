package com.sashimi.payment.application.usecase;


import com.sashimi.payment.application.command.PaymentCheckoutCommand;

import java.util.List;

public interface PaymentCommandUseCase {

    PaymentResult checkout(PaymentCheckoutCommand command);

    record PaymentResult(
            Long orderId,
            String orderNo,
            Long paymentId,
            Long amount,
            String status,
            List<PaidCourse> courses
    ) {
    }

    record PaidCourse(
            Long courseId,
            String title,
            Long price
    ) {
    }
}