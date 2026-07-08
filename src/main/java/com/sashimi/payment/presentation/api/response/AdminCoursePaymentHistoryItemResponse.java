package com.sashimi.payment.presentation.api.response;

import com.sashimi.payment.application.usecase.AdminPaymentQueryUseCase;

import java.time.LocalDateTime;
import java.util.List;

public record AdminCoursePaymentHistoryItemResponse(
        int rowNumber,
        String orderNo,
        String userName,
        String loginId,
        List<AdminCoursePaymentCourseResponse> courses,
        Long totalAmount,
        LocalDateTime paidAt
) {

    public static AdminCoursePaymentHistoryItemResponse from(
            AdminPaymentQueryUseCase.AdminCoursePaymentHistoryItem item
    ) {
        return new AdminCoursePaymentHistoryItemResponse(
                item.rowNumber(),
                item.orderNo(),
                item.userName(),
                item.loginId(),
                item.courses()
                        .stream()
                        .map(AdminCoursePaymentCourseResponse::from)
                        .toList(),
                item.totalAmount(),
                item.paidAt()
        );
    }
}