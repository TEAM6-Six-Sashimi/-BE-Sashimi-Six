package com.sashimi.resume.infrastructure.ai;

import com.sashimi.ai.domain.model.AiPrompt;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.resume.application.port.ResumeImprovementAiPort;
import com.sashimi.resume.application.result.SectionFeedbackResult;
import com.sashimi.resume.application.result.SectionScoreResult;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("local & !gemini")
public class StubResumeImprovementAiAdapter
        implements ResumeImprovementAiPort {

    @Override
    public List<SectionFeedbackResult> generate(
            List<SectionScoreResult> sections,
            AiPrompt prompt
    ) {
        if (sections == null || sections.isEmpty()) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_FEEDBACK
            );
        }

        if (sections.stream().anyMatch(
                section -> section == null
                        || section.score() >= 80
        )) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_FEEDBACK
            );
        }

        if (prompt == null) {
            throw new BusinessException(
                    ErrorCode.RESUME_INVALID_REVIEW_FEEDBACK
            );
        }

        return sections.stream()
                .map(section ->
                        SectionFeedbackResult.improvement(
                                section,
                                messageOf(section)
                        )
                )
                .toList();
    }

    private String messageOf(
            SectionScoreResult section
    ) {
        return switch (section.type()) {
            case EDUCATION ->
                    "학력 정보를 보완하면 이력서의 완성도를 높일 수 있습니다.";

            case CAREER ->
                    "경력 정보를 추가하거나 정리하면 경력 사항의 완성도를 높일 수 있습니다.";

            case CERTIFICATE ->
                    "검증된 자격증 정보를 추가하면 자격증 사항의 완성도를 높일 수 있습니다.";
        };
    }
}