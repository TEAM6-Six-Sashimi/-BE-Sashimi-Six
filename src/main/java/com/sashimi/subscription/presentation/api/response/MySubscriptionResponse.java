package com.sashimi.subscription.presentation.api.response;

import com.sashimi.subscription.application.usecase.SubscriptionQueryUseCase;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record MySubscriptionResponse(

        @Schema(description = "현재 구독 여부")
        boolean subscribed,

        @Schema(description = "구독 ID", nullable = true)
        Long subscriptionId,

        @Schema(description = "플랜 코드", nullable = true)
        String planCode,

        @Schema(description = "플랜명", nullable = true)
        String planName,

        @Schema(description = "구독 상태", nullable = true)
        String status,

        @Schema(description = "구독 시작일", nullable = true)
        LocalDateTime startedAt,

        @Schema(description = "현재 이용 만료일", nullable = true)
        LocalDateTime expiresAt,

        @Schema(description = "다음 자동 결제 예정일", nullable = true)
        LocalDateTime nextBillingAt,

        @Schema(description = "구독 결제 유예 종료일", nullable = true)
        LocalDateTime gracePeriodUntil,

        @Schema(description = "마지막 자동 갱신 실패 시각", nullable = true)
        LocalDateTime lastRenewalFailedAt,

        @Schema(description = "자동 갱신 재시도 횟수")
        int renewalRetryCount,

        @Schema(description = "자동 갱신 여부")
        boolean autoRenew,

        @Schema(description = "해지 신청 가능 여부")
        boolean cancellable


) {
    public static MySubscriptionResponse from(
            SubscriptionQueryUseCase.MySubscriptionResult result
    ) {
        return new MySubscriptionResponse(
                result.subscribed(),
                result.subscriptionId(),
                result.planCode(),
                result.planName(),
                result.status(),
                result.startedAt(),
                result.expiresAt(),
                result.nextBillingAt(),
                result.gracePeriodUntil(),
                result.lastRenewalFailedAt(),
                result.renewalRetryCount(),
                result.autoRenew(),
                result.cancellable()
        );
    }
}