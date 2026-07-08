package com.sashimi.payment.presentation.api.response;

import com.sashimi.payment.application.usecase.AdminPaymentQueryUseCase;

import java.util.List;

public record AdminCoursePaymentHistoryResponse(
        List<AdminCoursePaymentHistoryItemResponse> items,
        long totalElements,
        int totalPages,
        int page,
        int size
) {

    public static AdminCoursePaymentHistoryResponse from(
            AdminPaymentQueryUseCase.AdminCoursePaymentHistory result
    ) {
        return new AdminCoursePaymentHistoryResponse(
                result.items()
                        .stream()
                        .map(AdminCoursePaymentHistoryItemResponse::from)
                        .toList(),
                result.totalElements(),
                result.totalPages(),
                result.page(),
                result.size()
        );
    }
}