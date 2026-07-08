package com.sashimi.credit.presentation.api.response;

import com.sashimi.credit.application.usecase.AdminCreditQueryUseCase;

import java.time.LocalDateTime;

public record AdminCreditChargeHistoryItemResponse(
        int rowNumber,
        String orderNo,
        String loginId,
        Long chargedCredit,
        String paymentMethod,
        Long paidAmount,
        LocalDateTime approvedAt
) {

    public static AdminCreditChargeHistoryItemResponse from(
            AdminCreditQueryUseCase.AdminCreditChargeHistoryItem item
    ) {
        return new AdminCreditChargeHistoryItemResponse(
                item.rowNumber(),
                item.orderNo(),
                item.loginId(),
                item.chargedCredit(),
                item.paymentMethod(),
                item.paidAmount(),
                item.approvedAt()
        );
    }
}