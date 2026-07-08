package com.sashimi.payment.presentation.api.response;

import com.sashimi.payment.application.usecase.AdminPaymentQueryUseCase;

import java.time.LocalDateTime;

public record AdminSubscriptionPaymentHistoryItemResponse(
        int rowNumber,
        String orderNo,
        String userName,
        String loginId,
        String planCode,
        String planName,
        Long amount,
        LocalDateTime paidAt
) {

    public static AdminSubscriptionPaymentHistoryItemResponse from(
            AdminPaymentQueryUseCase.AdminSubscriptionPaymentHistoryItem item
    ) {
        return new AdminSubscriptionPaymentHistoryItemResponse(
                item.rowNumber(),
                item.orderNo(),
                item.userName(),
                item.loginId(),
                item.planCode(),
                item.planName(),
                item.amount(),
                item.paidAt()
        );
    }
}