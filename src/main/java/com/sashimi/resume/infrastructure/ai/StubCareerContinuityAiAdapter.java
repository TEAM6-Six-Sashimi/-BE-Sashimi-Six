package com.sashimi.resume.infrastructure.ai;

import com.sashimi.ai.domain.model.AiPrompt;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.resume.application.port.CareerContinuityAiPort;
import com.sashimi.resume.application.result.CareerContinuityResult;
import com.sashimi.resume.domain.model.ResumeCareer;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("local & !gemini")
public class StubCareerContinuityAiAdapter
        implements CareerContinuityAiPort {

    @Override
    public CareerContinuityResult evaluate(
            List<ResumeCareer> careers,
            AiPrompt prompt
    ) {
        if (careers == null || careers.size() < 2) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_SCORE
            );
        }

        return new CareerContinuityResult(20);
    }
}