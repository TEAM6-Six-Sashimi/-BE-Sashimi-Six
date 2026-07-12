package com.sashimi.ai.presentation.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AiUsageDataResponse(
        String label,

        @JsonProperty("job_analysis")
        long jobAnalysis,

        @JsonProperty("resume_evaluate")
        long resumeEvaluate,

        @JsonProperty("cover_letter_review")
        long coverLetterReview
) {
}