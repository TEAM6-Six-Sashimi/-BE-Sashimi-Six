package com.sashimi.review.application.service;

import com.sashimi.enrollment.application.port.EnrollmentPort;
import com.sashimi.enrollment.application.port.EnrollmentSummary;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.review.application.command.WriteReviewCommand;
import com.sashimi.review.application.port.CourseRatingPort;
import com.sashimi.review.application.usecase.ReviewCommandUseCase;
import com.sashimi.review.domain.model.Review;
import com.sashimi.review.domain.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewCommandService implements ReviewCommandUseCase {

    private final ReviewRepository reviewRepository;
    private final EnrollmentPort enrollmentPort;
    private final CourseRatingPort courseRatingPort;

    @Override
    public void deleteReview(Long userId, Long reviewId, boolean isAdmin) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.getUserId().equals(userId) && !isAdmin) {
            throw new BusinessException(ErrorCode.REVIEW_FORBIDDEN);
        }

        reviewRepository.save(review.delete());
        courseRatingPort.updateRating(review.getCourseId());
    }

    @Override
    public void writeReview(WriteReviewCommand command) {
        EnrollmentSummary enrollment = enrollmentPort
                .getEnrollmentByCourse(command.userId(), command.courseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_ENROLLED));

        if (enrollment.progressRate().compareTo(BigDecimal.ZERO) == 0) {
            throw new BusinessException(ErrorCode.REVIEW_PROGRESS_REQUIRED);
        }

        if (reviewRepository.existsByUserIdAndCourseId(command.userId(), command.courseId())) {
            throw new BusinessException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        Review review = Review.create(command.userId(), command.courseId(), command.rating(), command.content());
        reviewRepository.save(review);
        courseRatingPort.updateRating(command.courseId());
    }
}
