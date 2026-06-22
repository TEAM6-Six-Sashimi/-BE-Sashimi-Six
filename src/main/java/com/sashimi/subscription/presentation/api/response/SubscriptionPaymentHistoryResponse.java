package com.sashimi.subscription.presentation.api.response;

import com.sashimi.subscription.application.usecase.SubscriptionQueryUseCase;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record SubscriptionPaymentHistoryResponse(

        @Schema(description = "구독 결제 내역")
        List<SubscriptionPaymentHistoryItemResponse> items,

        @Schema(description = "전체 항목 수")
        long totalElements,

        @Schema(description = "전체 페이지 수")
        int totalPages,

        @Schema(description = "현재 페이지 번호")
        int page,

        @Schema(description = "페이지 크기")
        int size
) {
    public static SubscriptionPaymentHistoryResponse from(
            SubscriptionQueryUseCase.PaymentHistoryResult result
    ) {
        return new SubscriptionPaymentHistoryResponse(
                result.items()
                        .stream()
                        .map(
                                SubscriptionPaymentHistoryItemResponse
                                        ::from
                        )
                        .toList(),
                result.totalElements(),
                result.totalPages(),
                result.page(),
                result.size()
        );
    }
}