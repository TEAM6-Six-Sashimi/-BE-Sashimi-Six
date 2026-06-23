package com.sashimi.subscription.presentation.api.response;

import com.sashimi.subscription.application.usecase.SubscriptionCommandUseCase;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record CancelSubscriptionResponse(

        @Schema(description = "구독 ID")
        Long subscriptionId,

        @Schema(description = "플랜 코드")
        String planCode,

        @Schema(description = "현재 구독 상태")
        String status,

        @Schema(description = "자동 갱신 여부")
        boolean autoRenew,

        @Schema(description = "구독 이용 가능 종료일")
        LocalDateTime effectiveUntil
) {
    public static CancelSubscriptionResponse from(
            SubscriptionCommandUseCase.CancelResult result
    ) {
        return new CancelSubscriptionResponse(
                result.subscriptionId(),
                result.planCode(),
                result.status(),
                result.autoRenew(),
                result.effectiveUntil()
        );
    }
}