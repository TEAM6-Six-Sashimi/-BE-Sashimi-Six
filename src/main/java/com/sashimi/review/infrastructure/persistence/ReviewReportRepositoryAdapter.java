package com.sashimi.review.infrastructure.persistence;

import com.sashimi.review.domain.model.ReviewReport;
import com.sashimi.review.domain.repository.ReviewReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewReportRepositoryAdapter implements ReviewReportRepository {

    private final SpringDataReviewReportRepository springDataReviewReportRepository;

    @Override
    public ReviewReport save(ReviewReport reviewReport) {
        return springDataReviewReportRepository.save(ReviewReportJpaEntity.fromDomain(reviewReport)).toDomain();
    }

    @Override
    public boolean existsByReviewIdAndReporterId(Long reviewId, Long reporterId) {
        return springDataReviewReportRepository.existsByReviewIdAndReporterId(reviewId, reporterId);
    }
}
