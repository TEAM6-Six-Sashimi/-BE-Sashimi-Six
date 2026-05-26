package com.sashimi.payment.presentation.api.response;

import com.sashimi.payment.application.usecase.PaymentQueryUseCase;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentHistoryItemResponse(
        Long paymentId,
        Long orderId,
        String orderNo,
        BigDecimal amount,
        String paymentStatus,
        String orderStatus,
        LocalDateTime paidAt,
        LocalDateTime createdAt
) {
    public static PaymentHistoryItemResponse from(PaymentQueryUseCase.PaymentHistoryItem item) {
        return new PaymentHistoryItemResponse(
                item.paymentId(),
                item.orderId(),
                item.orderNo(),
                item.amount(),
                item.paymentStatus(),
                item.orderStatus(),
                item.paidAt(),
                item.createdAt()
        );
    }
}