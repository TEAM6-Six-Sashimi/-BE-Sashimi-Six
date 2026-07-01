package com.sashimi.resume.application.service;

import com.sashimi.ai.domain.model.AiPrompt;
import com.sashimi.ai.domain.model.AiPromptType;
import com.sashimi.ai.domain.repository.AiPromptRepository;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.resume.application.port.CareerContinuityAiPort;
import com.sashimi.resume.application.result.CareerContinuityResult;
import com.sashimi.resume.domain.model.EducationDegree;
import com.sashimi.resume.domain.model.EmploymentType;
import com.sashimi.resume.domain.model.GraduationStatus;
import com.sashimi.resume.domain.model.Resume;
import com.sashimi.resume.domain.model.ResumeCareer;
import com.sashimi.resume.domain.model.ResumeEducation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CareerContinuityEvaluatorTest {

    @Mock
    private AiPromptRepository aiPromptRepository;

    @Mock
    private CareerContinuityAiPort
            careerContinuityAiPort;

    private CareerContinuityEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new CareerContinuityEvaluator(
                aiPromptRepository,
                careerContinuityAiPort
        );
    }

    @Test
    void returnsZeroWithoutAiCallWhenCareerIsEmpty() {
        Resume resume = entryLevelResume();

        CareerContinuityResult result =
                evaluator.evaluate(resume);

        assertThat(result.score()).isZero();

        verifyNoInteractions(
                aiPromptRepository,
                careerContinuityAiPort
        );
    }

    @Test
    void returnsThirtyWithoutAiCallForSingleCareer() {
        Resume resume = resumeWithCareers(
                List.of(career("백엔드 개발자"))
        );

        CareerContinuityResult result =
                evaluator.evaluate(resume);

        assertThat(result.score()).isEqualTo(30);

        verifyNoInteractions(
                aiPromptRepository,
                careerContinuityAiPort
        );
    }

    @Test
    void callsAiForTwoOrMoreCareers() {
        Resume resume = resumeWithCareers(
                List.of(
                        career("백엔드 개발자"),
                        career("서버 개발자")
                )
        );

        AiPrompt prompt = continuityPrompt();

        when(
                aiPromptRepository.findActiveByType(
                        AiPromptType
                                .RESUME_CAREER_CONTINUITY
                )
        ).thenReturn(Optional.of(prompt));

        when(
                careerContinuityAiPort.evaluate(
                        resume.careers(),
                        prompt
                )
        ).thenReturn(
                new CareerContinuityResult(20)
        );

        CareerContinuityResult result =
                evaluator.evaluate(resume);

        assertThat(result.score()).isEqualTo(20);

        verify(aiPromptRepository)
                .findActiveByType(
                        AiPromptType
                                .RESUME_CAREER_CONTINUITY
                );

        verify(careerContinuityAiPort)
                .evaluate(
                        resume.careers(),
                        prompt
                );
    }

    @Test
    void throwsExceptionWhenContinuityPromptIsMissing() {
        Resume resume = resumeWithCareers(
                List.of(
                        career("백엔드 개발자"),
                        career("서버 개발자")
                )
        );

        when(
                aiPromptRepository.findActiveByType(
                        AiPromptType
                                .RESUME_CAREER_CONTINUITY
                )
        ).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                evaluator.evaluate(resume)
        )
                .isInstanceOf(
                        BusinessException.class
                );

        verifyNoInteractions(
                careerContinuityAiPort
        );
    }

    private Resume entryLevelResume() {
        return Resume.create(
                1L,
                List.of(education()),
                true,
                List.of(),
                List.of(),
                true
        ).withId(1L);
    }

    private Resume resumeWithCareers(
            List<ResumeCareer> careers
    ) {
        return Resume.create(
                1L,
                List.of(education()),
                false,
                careers,
                List.of(),
                true
        ).withId(1L);
    }

    private ResumeEducation education() {
        return new ResumeEducation(
                "한국대학교",
                YearMonth.of(2016, 3),
                YearMonth.of(2020, 2),
                GraduationStatus.GRADUATED,
                "컴퓨터공학과",
                EducationDegree.BACHELOR,
                null
        );
    }

    private ResumeCareer career(
            String jobTitle
    ) {
        return new ResumeCareer(
                "테스트 회사",
                YearMonth.of(2020, 1),
                YearMonth.of(2021, 1),
                false,
                EmploymentType.FULL_TIME,
                null,
                jobTitle
        );
    }

    private AiPrompt continuityPrompt() {
        return new AiPrompt(
                1L,
                "업무 연속성 평가",
                AiPromptType.RESUME_CAREER_CONTINUITY,
                "경력 목록: {careerHistory}",
                1,
                true
        );
    }
}