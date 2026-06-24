package com.sashimi.credit.presentation.api.response;

import com.sashimi.credit.application.usecase.CreditQueryUseCase;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record CreditChargeHistoryItemResponse(

        @Schema(
                description = "크레딧 충전 결제 ID",
                example = "1"
        )
        Long creditChargePaymentId,

        @Schema(
                description = "토스 결제 주문번호",
                example = "credit_1234567890abcdef"
        )
        String orderId,

        @Schema(
                description = "실제 결제 금액",
                example = "30000"
        )
        Long paidAmount,

        @Schema(
                description = "충전된 크레딧",
                example = "30000"
        )
        Long chargedCredit,

        @Schema(
                description = "결제 승인 일시",
                example = "2026-06-24T14:32:00"
        )
        LocalDateTime approvedAt,

        @Schema(
                description = "결제 수단",
                example = "카드"
        )
        String paymentMethod
) {
    public static CreditChargeHistoryItemResponse from(
            CreditQueryUseCase.CreditChargeHistoryItem item
    ) {
        return new CreditChargeHistoryItemResponse(
                item.creditChargePaymentId(),
                item.orderId(),
                item.paidAmount(),
                item.chargedCredit(),
                item.approvedAt(),
                item.paymentMethod()
        );
    }
}