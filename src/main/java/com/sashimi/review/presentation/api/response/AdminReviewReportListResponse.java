package com.sashimi.review.presentation.api.response;

import com.sashimi.review.application.usecase.AdminReviewReportQueryUseCase.ReportSummary;
import com.sashimi.review.domain.model.ReviewReportCategory;
import com.sashimi.review.domain.model.ReviewReportStatus;

import java.time.LocalDateTime;

public record AdminReviewReportListResponse(
        Long reportId,
        String reviewContent,
        String courseName,
        String writerLoginId,
        ReviewReportCategory category,
        LocalDateTime reportedAt,
        ReviewReportStatus status
) {
    public static AdminReviewReportListResponse from(ReportSummary summary) {
        return new AdminReviewReportListResponse(
                summary.reportId(),
                summary.reviewContent(),
                summary.courseName(),
                summary.writerLoginId(),
                summary.category(),
                summary.reportedAt(),
                summary.status()
        );
    }
}
