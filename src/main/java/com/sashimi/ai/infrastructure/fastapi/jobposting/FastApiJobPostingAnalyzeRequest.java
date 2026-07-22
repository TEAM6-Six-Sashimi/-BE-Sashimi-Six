package com.sashimi.ai.infrastructure.fastapi.jobposting;

public record FastApiJobPostingAnalyzeRequest(
        String content,
        boolean resumeBased,
        String resumeContent
) {
}