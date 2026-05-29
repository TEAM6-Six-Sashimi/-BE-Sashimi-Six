package com.sashimi.payment.presentation.api.response;

import com.sashimi.payment.application.usecase.PaymentCommandUseCase;

import java.util.List;

public record PaymentResponse(
        Long orderId,
        String orderNo,
        Long paymentId,
        Long amount,
        String status,
        List<PaidCourseResponse> courses
) {
    public static PaymentResponse from(PaymentCommandUseCase.PaymentResult result) {
        return new PaymentResponse(
                result.orderId(),
                result.orderNo(),
                result.paymentId(),
                result.amount(),
                result.status(),
                result.courses().stream().map(PaidCourseResponse::from).toList()
        );
    }
}