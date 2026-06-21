package com.sashimi.review.application.command;

import com.sashimi.review.domain.model.ReviewReportCategory;

public record ReportReviewCommand(
        Long userId,
        Long reviewId,
        ReviewReportCategory category,
        String reason
) {}
