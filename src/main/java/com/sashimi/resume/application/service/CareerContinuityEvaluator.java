package com.sashimi.resume.application.service;

import com.sashimi.ai.domain.model.AiPrompt;
import com.sashimi.ai.domain.model.AiPromptType;
import com.sashimi.ai.domain.repository.AiPromptRepository;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.resume.application.port.CareerContinuityAiPort;
import com.sashimi.resume.application.result.CareerContinuityResult;
import com.sashimi.resume.domain.model.Resume;
import com.sashimi.resume.domain.model.ResumeCareer;
import org.springframework.stereotype.Service;

import java.util.List;

// 이력서 항목 중 경력 개수에 따른 경력 연속성 점수
// 0개는 0점, 1개는 30점, 2개 이상은 AI 평가
@Service
public class CareerContinuityEvaluator {

    private final AiPromptRepository
            aiPromptRepository;

    private final CareerContinuityAiPort
            careerContinuityAiPort;

    public CareerContinuityEvaluator(
            AiPromptRepository aiPromptRepository,
            CareerContinuityAiPort careerContinuityAiPort
    ) {
        this.aiPromptRepository =
                aiPromptRepository;
        this.careerContinuityAiPort =
                careerContinuityAiPort;
    }

    public CareerContinuityResult evaluate(
            Resume resume
    ) {
        if (resume == null) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_SCORE
            );
        }

        List<ResumeCareer> careers =
                resume.careers();

        if (careers.isEmpty()) {
            return CareerContinuityResult
                    .noCareer();
        }

        if (careers.size() == 1) {
            return CareerContinuityResult
                    .singleCareer();
        }

        AiPrompt prompt = aiPromptRepository
                .findActiveByType(
                        AiPromptType
                                .RESUME_CAREER_CONTINUITY
                )
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode
                                        .AI_PROMPT_NOT_FOUND
                        )
                );

        return careerContinuityAiPort.evaluate(
                careers,
                prompt
        );
    }
}