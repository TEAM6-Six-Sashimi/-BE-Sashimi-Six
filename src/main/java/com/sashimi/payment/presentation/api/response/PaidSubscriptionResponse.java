package com.sashimi.payment.presentation.api.response;

import com.sashimi.payment.application.usecase.PaymentCommandUseCase;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record PaidSubscriptionResponse(

        @Schema(description = "구독 ID")
        Long subscriptionId,

        @Schema(description = "플랜 코드")
        String planCode,

        @Schema(description = "플랜명")
        String planName,

        @Schema(description = "구독 상태")
        String status,

        @Schema(description = "구독 시작일")
        LocalDateTime startedAt,

        @Schema(description = "구독 만료 예정일")
        LocalDateTime expiresAt,

        @Schema(description = "다음 자동 결제 예정일")
        LocalDateTime nextBillingAt
) {
    public static PaidSubscriptionResponse from(
            PaymentCommandUseCase.PaidSubscription result
    ) {
        return new PaidSubscriptionResponse(
                result.subscriptionId(),
                result.planCode(),
                result.planName(),
                result.status(),
                result.startedAt(),
                result.expiresAt(),
                result.nextBillingAt()
        );
    }
}