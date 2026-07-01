package com.sashimi.subscription.presentation.api.response;

import com.sashimi.subscription.application.usecase.SubscriptionQueryUseCase;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record SubscriptionPlanResponse(

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

        @Schema(description = "구독권 플랜 썸네일 URL", example = "/files/images?key=images%2F531e6874-8223-419c-b78c-a17ad449b853.png")
        String planThumbnail,

        @Schema(description = "플랜 제공 기능")
        List<String> features
) {
    public static SubscriptionPlanResponse from(
            SubscriptionQueryUseCase.PlanResult result
    ) {
        return new SubscriptionPlanResponse(
                result.planCode(),
                result.planName(),
                result.durationMonths(),
                result.originalPrice(),
                result.price(),
                result.discountRate(),
                result.planThumbnail(),
                result.features()
        );
    }
}