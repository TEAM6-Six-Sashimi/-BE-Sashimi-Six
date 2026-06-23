package com.sashimi.resume.application.port;

import com.sashimi.ai.domain.model.AiPrompt;
import com.sashimi.resume.application.result.SectionFeedbackResult;
import com.sashimi.resume.application.result.SectionScoreResult;

import java.util.List;

public interface ResumeImprovementAiPort {

    List<SectionFeedbackResult> generate(
            List<SectionScoreResult> sections,
            AiPrompt prompt
    );
}