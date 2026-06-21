package com.sashimi.review.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataReviewReportRepository extends JpaRepository<ReviewReportJpaEntity, Long> {

    boolean existsByReviewIdAndReporterId(Long reviewId, Long reporterId);
}
