package com.sashimi.resume.application.usecase;

import com.sashimi.resume.application.command.ReviewResumeCommand;
import com.sashimi.resume.domain.model.ResumeEvaluation;

public interface ReviewResumeUseCase {

    /**
     * 이력서를 AI로 평가하고, 평가 결과를 저장한 뒤 반환한다.
     */
    ResumeEvaluation review(ReviewResumeCommand command);
}
