package com.sashimi.review.domain.model;

import java.time.LocalDateTime;

public class ReviewReport {

    private final Long id;
    private final Long reviewId;
    private final Long reporterId;
    private final ReviewReportCategory category;
    private final String reason;
    private final ReviewReportStatus status;
    private final LocalDateTime createdAt;

    private ReviewReport(Long id, Long reviewId, Long reporterId,
                         ReviewReportCategory category, String reason,
                         ReviewReportStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.reviewId = reviewId;
        this.reporterId = reporterId;
        this.category = category;
        this.reason = reason;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static ReviewReport create(Long reviewId, Long reporterId,
                                      ReviewReportCategory category, String reason) {
        return new ReviewReport(null, reviewId, reporterId, category, reason,
                ReviewReportStatus.PENDING, LocalDateTime.now());
    }

    public static ReviewReport restore(Long id, Long reviewId, Long reporterId,
                                       ReviewReportCategory category, String reason,
                                       ReviewReportStatus status, LocalDateTime createdAt) {
        return new ReviewReport(id, reviewId, reporterId, category, reason, status, createdAt);
    }

    public Long getId() { return id; }
    public Long getReviewId() { return reviewId; }
    public Long getReporterId() { return reporterId; }
    public ReviewReportCategory getCategory() { return category; }
    public String getReason() { return reason; }
    public ReviewReportStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
