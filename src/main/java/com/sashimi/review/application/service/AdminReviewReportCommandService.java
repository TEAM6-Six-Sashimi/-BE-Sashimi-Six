package com.sashimi.review.application.service;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.review.application.usecase.AdminReviewReportCommandUseCase;
import com.sashimi.review.domain.model.Review;
import com.sashimi.review.domain.model.ReviewReport;
import com.sashimi.review.domain.repository.ReviewReportRepository;
import com.sashimi.review.domain.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminReviewReportCommandService implements AdminReviewReportCommandUseCase {

    private final ReviewReportRepository reviewReportRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public void deleteReportedReview(Long reportId) {
        ReviewReport report = reviewReportRepository.findById(reportId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        Review review = reviewRepository.findById(report.getReviewId())
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        reviewRepository.save(review.delete());
        reviewReportRepository.save(report.process());
    }

    @Override
    public void rejectReport(Long reportId) {
        ReviewReport report = reviewReportRepository.findById(reportId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        reviewReportRepository.save(report.process());
    }
}
