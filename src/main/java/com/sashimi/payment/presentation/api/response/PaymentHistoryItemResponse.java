package com.sashimi.payment.presentation.api.response;

import com.sashimi.payment.application.usecase.PaymentQueryUseCase;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record PaymentHistoryItemResponse(
        Long paymentId,
        Long orderId,
        String orderNo,
        Long amount,
        String paymentStatus,
        String orderStatus,
        LocalDateTime paidAt,
        LocalDateTime createdAt,

        @Schema(description = "결제에 포함된 강의 목록")
        List<PaymentHistoryCourseResponse> courses
) {
    public static PaymentHistoryItemResponse from(
            PaymentQueryUseCase.PaymentHistoryItem item
    ) {
        return new PaymentHistoryItemResponse(
                item.paymentId(),
                item.orderId(),
                item.orderNo(),
                item.amount(),
                item.paymentStatus(),
                item.orderStatus(),
                item.paidAt(),
                item.createdAt(),
                item.courses().stream()
                        .map(PaymentHistoryCourseResponse::from)
                        .toList()
        );
    }
}