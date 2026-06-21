package com.sashimi.review.application.usecase;

import com.sashimi.review.application.command.WriteReviewCommand;

public interface ReviewCommandUseCase {

    void writeReview(WriteReviewCommand command);

    void deleteReview(Long userId, Long reviewId, boolean isAdmin);
}
