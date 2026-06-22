package com.sashimi.subscription.presentation.api.response;

import com.sashimi.subscription.application.usecase.SubscriptionQueryUseCase;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record SubscriptionPreviewResponse(

        @Schema(description = "플랜 코드", example = "MONTHLY")
        String planCode,

        @Schema(description = "플랜명", example = "1개월 플랜")
        String planName,

        @Schema(description = "구독 기간(개월)", example = "1")
        int durationMonths,

        @Schema(description = "할인 전 가격", example = "10000")
        Long originalPrice,

        @Schema(description = "실제 결제 가격", example = "10000")
        Long price,

        @Schema(description = "할인율", example = "0")
        int discountRate,

        @Schema(description = "플랜 제공 기능")
        List<String> features,

        @Schema(description = "현재 보유 크레딧", example = "30000")
        Long creditBalance,

        @Schema(description = "결제 후 예상 잔액", example = "20000")
        Long balanceAfterPayment,

        @Schema(description = "부족한 크레딧", example = "0")
        Long insufficientAmount,

        @Schema(description = "현재 활성 구독 보유 여부")
        boolean alreadySubscribed,

        @Schema(description = "구매 가능 여부")
        boolean purchasable
) {
    public static SubscriptionPreviewResponse from(
            SubscriptionQueryUseCase.PreviewResult result
    ) {
        SubscriptionQueryUseCase.PlanResult plan =
                result.plan();

        return new SubscriptionPreviewResponse(
                plan.planCode(),
                plan.planName(),
                plan.durationMonths(),
                plan.originalPrice(),
                plan.price(),
                plan.discountRate(),
                plan.features(),
                result.creditBalance(),
                result.balanceAfterPayment(),
                result.insufficientAmount(),
                result.alreadySubscribed(),
                result.purchasable()
        );
    }
}