package com.sashimi.credit.presentation.api.response;

import com.sashimi.credit.application.usecase.AdminCreditQueryUseCase;

import java.util.List;

public record AdminCreditChargeHistoryResponse(
        List<AdminCreditChargeHistoryItemResponse> items,
        long totalElements,
        int totalPages,
        int page,
        int size
) {

    public static AdminCreditChargeHistoryResponse from(
            AdminCreditQueryUseCase.AdminCreditChargeHistory result
    ) {
        return new AdminCreditChargeHistoryResponse(
                result.items()
                        .stream()
                        .map(AdminCreditChargeHistoryItemResponse::from)
                        .toList(),
                result.totalElements(),
                result.totalPages(),
                result.page(),
                result.size()
        );
    }
}