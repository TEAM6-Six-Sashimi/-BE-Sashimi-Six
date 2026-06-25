package com.sashimi.enrollment.application.usecase;

import java.math.BigDecimal;

public interface LearningProgressCommandUseCase {

    ProgressResult reportProgress(Long userId, Long courseId, Long sessionId, int lastPositionSeconds);

    record ProgressResult(
            BigDecimal sessionProgressRate,
            boolean sessionCompleted,
            BigDecimal courseProgressRate,
            boolean courseCompleted
    ) {}
}
