package com.sashimi.coverletter.application.service;

import com.sashimi.ai.infrastructure.fastapi.coverletter.FastApiCoverLetterReviewRequest;
import com.sashimi.coverletter.domain.model.CoverLetterQuestion;

import java.time.LocalDateTime;
import java.util.Map;

public record CoverLetterReviewPreparation(
        Long historyId,
        LocalDateTime createdAt,
        Map<CoverLetterQuestion, String> contentMap,
        FastApiCoverLetterReviewRequest fastApiRequest
) {
}