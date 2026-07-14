package com.sashimi.coverletter.presentation.api.response;

import java.time.LocalDateTime;

public record LatestCoverLetterReviewSummaryResponse(
        ReviewSummary review
) {

    public record ReviewSummary(
            Long reviewId,
            LocalDateTime createdAt,
            CoverLetterReviewSummaryResponse summary
    ) {
    }

    public static LatestCoverLetterReviewSummaryResponse empty() {
        return new LatestCoverLetterReviewSummaryResponse(null);
    }

    public static LatestCoverLetterReviewSummaryResponse of(
            Long reviewId,
            LocalDateTime createdAt,
            CoverLetterReviewSummaryResponse summary
    ) {
        return new LatestCoverLetterReviewSummaryResponse(
                new ReviewSummary(
                        reviewId,
                        createdAt,
                        summary
                )
        );
    }
}