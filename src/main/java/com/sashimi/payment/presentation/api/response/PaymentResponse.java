package com.sashimi.payment.presentation.api.response;

import com.sashimi.payment.application.command.PaymentPurchaseType;
import com.sashimi.payment.application.usecase.PaymentCommandUseCase;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PaymentResponse(

        @Schema(description = "내부 주문 ID")
        Long orderId,

        @Schema(description = "사용자 표시 주문번호")
        String orderNo,

        @Schema(description = "내부 결제 ID")
        Long paymentId,

        @Schema(description = "결제 상품 유형")
        PaymentPurchaseType purchaseType,

        @Schema(description = "실제 차감 크레딧")
        Long amount,

        @Schema(description = "결제 상태")
        String status,

        @Schema(description = "결제 후 크레딧 잔액")
        Long creditBalance,

        @Schema(description = "구매 완료 강의 목록")
        List<PaidCourseResponse> courses,

        @Schema(
                description = "구독권 결제 결과",
                nullable = true
        )
        PaidSubscriptionResponse subscription
) {
    public static PaymentResponse from(
            PaymentCommandUseCase.PaymentResult result
    ) {
        PaidSubscriptionResponse subscriptionResponse =
                result.subscription() == null
                        ? null
                        : PaidSubscriptionResponse.from(
                        result.subscription()
                );

        return new PaymentResponse(
                result.orderId(),
                result.orderNo(),
                result.paymentId(),
                result.purchaseType(),
                result.amount(),
                result.status(),
                result.creditBalance(),
                result.courses()
                        .stream()
                        .map(PaidCourseResponse::from)
                        .toList(),
                subscriptionResponse
        );
    }
}