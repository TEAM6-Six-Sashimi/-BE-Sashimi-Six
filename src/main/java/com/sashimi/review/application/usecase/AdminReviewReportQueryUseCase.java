package com.sashimi.review.application.usecase;

import com.sashimi.review.domain.model.ReviewReportCategory;
import com.sashimi.review.domain.model.ReviewReportStatus;
import com.sashimi.review.domain.model.ReviewStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminReviewReportQueryUseCase {

    List<ReportSummary> getReports(ReviewReportStatus status);

    ReportDetail getReport(Long reportId);

    record ReportSummary(
            Long reportId,
            String reviewContent,
            String courseName,
            String writerLoginId,
            ReviewReportCategory category,
            LocalDateTime reportedAt,
            ReviewReportStatus status
    ) {}

    record ReportDetail(
            String reviewContent,
            String writerLoginId,
            String reporterLoginId,
            LocalDateTime reportedAt,
            ReviewReportCategory category,
            String reason,
            ReviewStatus reviewStatus,
            ReviewReportStatus reportStatus
    ) {}
}
