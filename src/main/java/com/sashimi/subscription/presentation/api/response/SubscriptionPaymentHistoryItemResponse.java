package com.sashimi.subscription.presentation.api.response;

import com.sashimi.subscription.application.usecase.SubscriptionQueryUseCase;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record SubscriptionPaymentHistoryItemResponse(

        @Schema(description = "구독 결제 내역 ID")
        Long subscriptionPaymentId,

        @Schema(description = "구독 ID")
        Long subscriptionId,

        @Schema(description = "주문 ID")
        Long orderId,

        @Schema(description = "주문번호")
        String orderNo,

        @Schema(description = "플랜 코드")
        String planCode,

        @Schema(description = "플랜명")
        String planName,

        @Schema(description = "결제 크레딧")
        Long amount,

        @Schema(description = "결제 유형")
        String billingType,

        @Schema(description = "결제 완료일")
        LocalDateTime paidAt
) {
    public static SubscriptionPaymentHistoryItemResponse from(
            SubscriptionQueryUseCase.PaymentHistoryItem result
    ) {
        return new SubscriptionPaymentHistoryItemResponse(
                result.subscriptionPaymentId(),
                result.subscriptionId(),
                result.orderId(),
                result.orderNo(),
                result.planCode(),
                result.planName(),
                result.amount(),
                result.billingType(),
                result.paidAt()
        );
    }
}