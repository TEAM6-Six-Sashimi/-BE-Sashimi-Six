package com.sashimi.resume.application.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ResumeEvaluatedEvent(
        Long userId,
        Long resumeId,
        Long evaluationId,
        Long jobPostingId,
        BigDecimal overallScore,
        LocalDateTime evaluatedAt
) {
}