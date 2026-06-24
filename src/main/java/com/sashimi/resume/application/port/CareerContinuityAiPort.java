package com.sashimi.resume.application.port;

import com.sashimi.ai.domain.model.AiPrompt;
import com.sashimi.resume.application.result.CareerContinuityResult;
import com.sashimi.resume.domain.model.ResumeCareer;

import java.util.List;

/** 경력이 null 일 경우, AI 불러오지 않음 */
public interface CareerContinuityAiPort {

    CareerContinuityResult evaluate(
            List<ResumeCareer> careers,
            AiPrompt prompt
    );
}