package com.sashimi.review.domain.repository;

import com.sashimi.review.domain.model.ReviewReport;
import com.sashimi.review.domain.model.ReviewReportStatus;

import java.util.List;
import java.util.Optional;

public interface ReviewReportRepository {

    ReviewReport save(ReviewReport reviewReport);

    boolean existsByReviewIdAndReporterId(Long reviewId, Long reporterId);

    List<ReviewReport> findAll();

    List<ReviewReport> findAllByStatus(ReviewReportStatus status);

    List<ReviewReport> findAllByReviewIdAndStatus(Long reviewId, ReviewReportStatus status);

    Optional<ReviewReport> findById(Long reportId);
}
