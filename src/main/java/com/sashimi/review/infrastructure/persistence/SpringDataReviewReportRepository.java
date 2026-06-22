package com.sashimi.review.infrastructure.persistence;

import com.sashimi.review.domain.model.ReviewReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataReviewReportRepository extends JpaRepository<ReviewReportJpaEntity, Long> {

    boolean existsByReviewIdAndReporterId(Long reviewId, Long reporterId);

    List<ReviewReportJpaEntity> findAllByStatus(ReviewReportStatus status);
}
