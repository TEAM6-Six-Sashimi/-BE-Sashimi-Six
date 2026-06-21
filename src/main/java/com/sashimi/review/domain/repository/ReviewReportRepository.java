package com.sashimi.review.domain.repository;

import com.sashimi.review.domain.model.ReviewReport;

public interface ReviewReportRepository {

    ReviewReport save(ReviewReport reviewReport);

    boolean existsByReviewIdAndReporterId(Long reviewId, Long reporterId);
}
