package com.sashimi.payment.presentation.api.response;

import com.sashimi.payment.application.usecase.AdminPaymentQueryUseCase;

import java.util.List;

public record AdminSubscriptionPaymentHistoryResponse(
        List<AdminSubscriptionPaymentHistoryItemResponse> items,
        long totalElements,
        int totalPages,
        int page,
        int size
) {

    public static AdminSubscriptionPaymentHistoryResponse from(
            AdminPaymentQueryUseCase.AdminSubscriptionPaymentHistory result
    ) {
        return new AdminSubscriptionPaymentHistoryResponse(
                result.items()
                        .stream()
                        .map(AdminSubscriptionPaymentHistoryItemResponse::from)
                        .toList(),
                result.totalElements(),
                result.totalPages(),
                result.page(),
                result.size()
        );
    }
}