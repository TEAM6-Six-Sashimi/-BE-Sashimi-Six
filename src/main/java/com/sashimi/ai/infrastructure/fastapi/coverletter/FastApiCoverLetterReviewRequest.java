package com.sashimi.ai.infrastructure.fastapi.coverletter;

import java.util.List;

public record FastApiCoverLetterReviewRequest(
        List<Question> questions,
        String resumeSummary,
        String jobPostingSummary
) {
    public record Question(
            String questionKey,
            String questionTitle,
            String content
    ) {
    }
}