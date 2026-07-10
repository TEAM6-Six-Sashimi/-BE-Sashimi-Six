package com.sashimi.review.infrastructure.persistence;

import com.sashimi.review.domain.model.ReviewReport;
import com.sashimi.review.domain.model.ReviewReportStatus;
import com.sashimi.review.domain.repository.ReviewReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    @Override
    public List<ReviewReport> findAll() {
        return springDataReviewReportRepository.findAll().stream()
                .map(ReviewReportJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<ReviewReport> findAllByStatus(ReviewReportStatus status) {
        return springDataReviewReportRepository.findAllByStatus(status).stream()
                .map(ReviewReportJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<ReviewReport> findAllByReviewIdAndStatus(Long reviewId, ReviewReportStatus status) {
        return springDataReviewReportRepository.findAllByReviewIdAndStatus(reviewId, status).stream()
                .map(ReviewReportJpaEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<ReviewReport> findById(Long reportId) {
        return springDataReviewReportRepository.findById(reportId)
                .map(ReviewReportJpaEntity::toDomain);
    }
}
