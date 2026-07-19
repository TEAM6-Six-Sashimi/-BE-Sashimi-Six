package com.sashimi.resume.presentation.api.response;

import com.sashimi.resume.application.result.ReviewResumeResult;

public record LatestResumeReviewResponse(
        Long resumeId,
        ReviewResumeResponse review
) {
    public static LatestResumeReviewResponse of(
            Long resumeId,
            ReviewResumeResult result
    ) {
        return new LatestResumeReviewResponse(
                resumeId,
                ReviewResumeResponse.from(result)
        );
    }

    public static LatestResumeReviewResponse empty(
            Long resumeId
    ) {
        return new LatestResumeReviewResponse(
                resumeId,
                null
        );
    }
}