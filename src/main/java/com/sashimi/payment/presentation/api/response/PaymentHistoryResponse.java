package com.sashimi.payment.presentation.api.response;

import com.sashimi.payment.application.usecase.PaymentQueryUseCase;

import java.util.List;

public record PaymentHistoryResponse(List<PaymentHistoryItemResponse> items) {

    public static PaymentHistoryResponse from(PaymentQueryUseCase.PaymentHistory history) {
        return new PaymentHistoryResponse(
                history.items().stream().map(PaymentHistoryItemResponse::from).toList()
        );
    }
}