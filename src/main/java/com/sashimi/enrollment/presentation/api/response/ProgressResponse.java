package com.sashimi.enrollment.presentation.api.response;

import com.sashimi.enrollment.application.usecase.LearningProgressCommandUseCase.ProgressResult;

import java.math.BigDecimal;

public record ProgressResponse(
        BigDecimal sessionProgressRate,
        boolean sessionCompleted,
        BigDecimal courseProgressRate,
        boolean courseCompleted
) {
    public static ProgressResponse from(ProgressResult result) {
        return new ProgressResponse(
                result.sessionProgressRate(),
                result.sessionCompleted(),
                result.courseProgressRate(),
                result.courseCompleted()
        );
    }
}
