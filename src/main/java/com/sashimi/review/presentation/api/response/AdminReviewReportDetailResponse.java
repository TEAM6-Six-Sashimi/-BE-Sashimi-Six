package com.sashimi.review.presentation.api.response;

import com.sashimi.review.application.usecase.AdminReviewReportQueryUseCase.ReportDetail;
import com.sashimi.review.domain.model.ReviewReportCategory;
import com.sashimi.review.domain.model.ReviewReportStatus;
import com.sashimi.review.domain.model.ReviewStatus;

import java.time.LocalDateTime;

public record AdminReviewReportDetailResponse(
        String reviewContent,
        String writerLoginId,
        String reporterLoginId,
        LocalDateTime reportedAt,
        ReviewReportCategory category,
        String reason,
        ReviewStatus reviewStatus,
        ReviewReportStatus reportStatus
) {
    public static AdminReviewReportDetailResponse from(ReportDetail detail) {
        return new AdminReviewReportDetailResponse(
                detail.reviewContent(),
                detail.writerLoginId(),
                detail.reporterLoginId(),
                detail.reportedAt(),
                detail.category(),
                detail.reason(),
                detail.reviewStatus(),
                detail.reportStatus()
        );
    }
}
