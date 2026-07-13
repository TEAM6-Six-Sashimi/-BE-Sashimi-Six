package com.sashimi.coverletter.presentation.api.response;

import java.util.List;

public record CoverLetterReviewQuestionResponse(
        int questionNumber,
        String questionKey,
        String questionTitle,
        int maxLength,
        String status,
        String summaryFeedback,
        int spellingCorrectionCount,
        int expressionImprovementCount,
        int flowImprovementCount,
        String originalContent,
        List<CoverLetterSpellingCorrectionResponse> spellingCorrections,
        String feedback,
        String improvedExample
) {
}