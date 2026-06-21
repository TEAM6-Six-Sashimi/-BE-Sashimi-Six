package com.sashimi.review.application.service;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.review.application.command.ReportReviewCommand;
import com.sashimi.review.application.usecase.ReviewReportCommandUseCase;
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
public class ReviewReportCommandService implements ReviewReportCommandUseCase {

    private final ReviewRepository reviewRepository;
    private final ReviewReportRepository reviewReportRepository;

    @Override
    public void reportReview(ReportReviewCommand command) {
        Review review = reviewRepository.findById(command.reviewId())
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        if (review.getUserId().equals(command.userId())) {
            throw new BusinessException(ErrorCode.REVIEW_SELF_REPORT_FORBIDDEN);
        }

        if (reviewReportRepository.existsByReviewIdAndReporterId(command.reviewId(), command.userId())) {
            throw new BusinessException(ErrorCode.REVIEW_ALREADY_REPORTED);
        }

        reviewReportRepository.save(
                ReviewReport.create(command.reviewId(), command.userId(), command.category(), command.reason())
        );
    }
}
