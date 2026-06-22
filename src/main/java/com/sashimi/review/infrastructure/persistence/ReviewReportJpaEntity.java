package com.sashimi.review.infrastructure.persistence;

import com.sashimi.review.domain.model.ReviewReport;
import com.sashimi.review.domain.model.ReviewReportCategory;
import com.sashimi.review.domain.model.ReviewReportStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_reports")
public class ReviewReportJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @Column(name = "review_id", nullable = false)
    private Long reviewId;

    @Column(name = "reporter_id", nullable = false)
    private Long reporterId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ReviewReportCategory category;

    @Column(name = "reason", length = 200)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReviewReportStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected ReviewReportJpaEntity() {}

    public ReviewReportJpaEntity(Long reviewId, Long reporterId, ReviewReportCategory category,
                                  String reason, ReviewReportStatus status, LocalDateTime createdAt) {
        this.reviewId = reviewId;
        this.reporterId = reporterId;
        this.category = category;
        this.reason = reason;
        this.status = status;
        this.createdAt = createdAt;
    }

    public ReviewReport toDomain() {
        return ReviewReport.restore(id, reviewId, reporterId, category, reason, status, createdAt);
    }

    public static ReviewReportJpaEntity fromDomain(ReviewReport report) {
        return new ReviewReportJpaEntity(
                report.getReviewId(), report.getReporterId(), report.getCategory(),
                report.getReason(), report.getStatus(), report.getCreatedAt()
        );
    }
}
