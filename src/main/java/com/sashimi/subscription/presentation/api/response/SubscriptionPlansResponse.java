package com.sashimi.subscription.presentation.api.response;

import com.sashimi.subscription.application.usecase.SubscriptionQueryUseCase;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record SubscriptionPlansResponse(

        @Schema(description = "AI 구독권 플랜 목록")
        List<SubscriptionPlanResponse> plans
) {
    public static SubscriptionPlansResponse from(
            List<SubscriptionQueryUseCase.PlanResult> results
    ) {
        return new SubscriptionPlansResponse(
                results.stream()
                        .map(SubscriptionPlanResponse::from)
                        .toList()
        );
    }
}