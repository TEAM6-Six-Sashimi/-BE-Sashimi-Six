package com.sashimi.payment.presentation.api.request;

import com.sashimi.payment.application.command.PaymentPurchaseType;
import com.sashimi.subscription.domain.model.SubscriptionPlan;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentCheckoutRequest(

        @Schema(
                description = "결제 상품 유형",
                example = "AI_SUBSCRIPTION",
                allowableValues = {
                        "COURSE",
                        "CART",
                        "AI_SUBSCRIPTION"
                }
        )
        @NotNull(message = "결제 상품 유형은 필수입니다.")
        PaymentPurchaseType purchaseType,

        @Schema(
                description = "단일 강의 결제 시 강의 ID",
                example = "10",
                nullable = true
        )
        @Positive(message = "강의 ID는 양수여야 합니다.")
        Long courseId,

        @Schema(
                description = "AI 구독권 결제 시 플랜 코드",
                example = "MONTHLY",
                allowableValues = {
                        "MONTHLY",
                        "ANNUAL"
                },
                nullable = true
        )
        SubscriptionPlan planCode,

        @Schema(
                description = "결제 약관 동의 여부",
                example = "true"
        )
        @NotNull(message = "결제 동의 여부는 필수입니다.")
        @AssertTrue(
                message = "결제 진행을 위해 결제 동의가 필요합니다."
        )
        Boolean agreed
) {
}