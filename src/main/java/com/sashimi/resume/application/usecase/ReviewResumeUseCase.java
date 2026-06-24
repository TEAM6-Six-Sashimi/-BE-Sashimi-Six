package com.sashimi.resume.application.usecase;

import com.sashimi.resume.application.result.ReviewResumeResult;

public interface ReviewResumeUseCase {

    ReviewResumeResult review(
            Long resumeId,
            Long userId
    );
}