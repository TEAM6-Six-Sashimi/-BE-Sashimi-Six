package com.sashimi.resume.application.service;

import com.sashimi.ai.domain.model.AiPrompt;
import com.sashimi.ai.domain.model.AiPromptType;
import com.sashimi.ai.domain.repository.AiPromptRepository;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.resume.application.command.CreateResumeCommand;
import com.sashimi.resume.application.command.ReviewResumeCommand;
import com.sashimi.resume.application.event.ResumeEvaluatedEvent;
import com.sashimi.resume.application.port.ResumeAiReviewPort;
import com.sashimi.resume.application.port.ResumeAiReviewResult;
import com.sashimi.resume.domain.model.Resume;
import com.sashimi.resume.domain.model.ResumeEvaluation;
import com.sashimi.resume.domain.repository.ResumeEvaluationRepository;
import com.sashimi.resume.domain.repository.ResumeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ResumeCommandServiceTest {

    private ResumeRepository resumeRepository;
    private ResumeEvaluationRepository resumeEvaluationRepository;
    private AiPromptRepository aiPromptRepository;
    private ResumeAiReviewPort resumeAiReviewPort;
    private ApplicationEventPublisher eventPublisher;

    private ResumeCommandService resumeCommandService;

    @BeforeEach
    void setUp() {
        // Service가 의존하는 Port들을 Mock으로 대체한다.
        // 테스트 대상은 ResumeCommandService의 흐름 제어이므로 실제 DB나 AI 호출은 수행하지 않는다.
        resumeRepository = mock(ResumeRepository.class);
        resumeEvaluationRepository = mock(ResumeEvaluationRepository.class);
        aiPromptRepository = mock(AiPromptRepository.class);
        resumeAiReviewPort = mock(ResumeAiReviewPort.class);
        eventPublisher = mock(ApplicationEventPublisher.class);

        // 생성자 주입 방식이므로 테스트에서도 필요한 의존성을 직접 주입한다.
        resumeCommandService = new ResumeCommandService(
                resumeRepository,
                resumeEvaluationRepository,
                aiPromptRepository,
                resumeAiReviewPort,
                eventPublisher
        );
    }

    @Test
    void createResume() {
        // given
        // 이력서 생성은 더 이상 templateType을 받지 않는다.
        // 이력서의 기본 정보, 학력, 경력, 기술 정보는 content JSON 안에 저장한다.
        // 이력서 생성 요청 Command 준비
        CreateResumeCommand command = new CreateResumeCommand(
                1L,
                "Backend Resume",
                "{\"basic\":{\"name\":\"박학생\"},\"skills\":[\"Java\",\"Spring Boot\"]}",
                true
        );

        // Repository 저장 후 반환될 이력서 도메인 객체 준비
        Resume savedResume = Resume.create(
                1L,
                "Backend Resume",
                "{\"basic\":{\"name\":\"박학생\"},\"skills\":[\"Java\",\"Spring Boot\"]}",
                true
        ).withId(10L);

        when(resumeRepository.save(any(Resume.class))).thenReturn(savedResume);

        // when
        Resume result = resumeCommandService.create(command);

        // then
        // 저장된 이력서가 서비스 결과로 그대로 반환되는지 확인
        assertThat(result.resumeId()).isEqualTo(10L);
        assertThat(result.userId()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("Backend Resume");
        assertThat(result.content()).contains("Spring Boot");
        assertThat(result.defaultResume()).isTrue();

        // Service가 Repository save를 호출했는지 확인
        verify(resumeRepository).save(any(Resume.class));
    }

    @Test
    void reviewResume() {
        // given
        // 사용자가 작성한 이력서
        // 사용자가 소유한 이력서를 조회했다고 가정
        Resume resume = new Resume(
                1L,
                1L,
                "Backend Resume",
                "{\"basic\":{\"name\":\"박학생\"},\"skills\":[\"Java\",\"Spring Boot\"]}"
        );

        // 이력서 평가에 사용할 활성 AI 프롬프트
        // DB 에서 조회된 활성 AI 프롬프트 준비
        AiPrompt prompt = new AiPrompt(
                1L,
                "Resume Review Prompt",
                AiPromptType.RESUME_REVIEW,
                "Review this resume: {resumeContent}",
                1,
                true
        );

        // AI Adapter가 반환했다고 가정하는 평가 결과
        // 실제 AI 호출 결과 대신 Port가 반환할 평가 결과 준비
        ResumeAiReviewResult aiResult = new ResumeAiReviewResult(
                BigDecimal.valueOf(82),
                "Strong Java/Spring experience",
                "Needs cloud experience",
                "Add AWS deployment project",
                "{\"overallScore\":82,\"sectionScores\":[],\"improvementItems\":[]}"
        );

        // Repository에 저장된 후 ID가 부여된 평가 결과
        // 평가 결과 저장 후 ID가 부여된 도메인 객체 준비
        ResumeEvaluation savedEvaluation = ResumeEvaluation.evaluated(
                aiResult.overallScore(),
                aiResult.strengths(),
                aiResult.weaknesses(),
                aiResult.suggestions(),
                aiResult.aiResult(),
                1L,
                null,
                1L
        ).withId(100L);

        when(resumeRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(resume));
        when(aiPromptRepository.findActiveByType(AiPromptType.RESUME_REVIEW))
                .thenReturn(Optional.of(prompt));
        when(resumeAiReviewPort.review(resume, prompt))
                .thenReturn(aiResult);
        when(resumeEvaluationRepository.save(any(ResumeEvaluation.class)))
                .thenReturn(savedEvaluation);

        // when
        ResumeEvaluation result = resumeCommandService.review(
                new ReviewResumeCommand(1L, 1L, null)
        );

        // then
        // AI 평가 결과가 저장된 평가 도메인으로 변환되어 반환되는지 확인
        assertThat(result.evaluationId()).isEqualTo(100L);
        assertThat(result.overallScore()).isEqualByComparingTo("82");
        assertThat(result.strengths()).isEqualTo("Strong Java/Spring experience");
        assertThat(result.aiResult()).contains("sectionScores");

        // 이력서 조회 -> 프롬프트 조회 -> AI 평가 -> 평가 결과 저장 순서의 핵심 협력 객체 호출 검증
        verify(resumeRepository).findByIdAndUserId(1L, 1L);
        verify(aiPromptRepository).findActiveByType(AiPromptType.RESUME_REVIEW);
        verify(resumeAiReviewPort).review(resume, prompt);
        verify(resumeEvaluationRepository).save(any(ResumeEvaluation.class));
        // 평가 저장 이후 후속 확장을 위한 ResumeEvaluatedEvent가 발행되는지 확인
        verify(eventPublisher).publishEvent(any(ResumeEvaluatedEvent.class));
    }

    @Test
    void reviewResumeThrowsWhenResumeNotFound() {
        // given
        // 요청한 사용자의 이력서를 찾지 못하는 상황
        // 요청한 사용자의 이력서를 찾지 못한 상황
        when(resumeRepository.findByIdAndUserId(999L, 1L))
                .thenReturn(Optional.empty());

        // when & then
        // 이력서가 없으면 RESUME_NOT_FOUND BusinessException이 발생
        assertThatThrownBy(() ->
                resumeCommandService.review(new ReviewResumeCommand(1L, 999L, null))
        ).isInstanceOfSatisfying(BusinessException.class, exception ->
                assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RESUME_NOT_FOUND)
        );

        // 이력서 조회에서 실패했으므로 이후 단계는 실행되면 안됨
        verify(resumeRepository).findByIdAndUserId(999L, 1L);
        verifyNoInteractions(aiPromptRepository);
        verifyNoInteractions(resumeAiReviewPort);
        verifyNoInteractions(resumeEvaluationRepository);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void reviewResumeThrowsWhenActivePromptNotFound() {
        // given
        // 이력서는 있지만, 사용할 수 있는 활성 AI 프롬프트가 없는 상황
        // 이력서는 존재하지만, 사용할 활성 AI 프롬프트가 없는 상황
        Resume resume = new Resume(
                1L,
                1L,
                "Backend Resume",
                "{\"basic\":{\"name\":\"박학생\"},\"skills\":[\"Java\",\"Spring Boot\"]}"
        );

        when(resumeRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(resume));
        when(aiPromptRepository.findActiveByType(AiPromptType.RESUME_REVIEW))
                .thenReturn(Optional.empty());

        // when & then
        // 활성 프롬프트가 없으면 AI_PROMPT_NOT_FOUND BusinessException이 발생
        assertThatThrownBy(() ->
                resumeCommandService.review(new ReviewResumeCommand(1L, 1L, null))
        ).isInstanceOfSatisfying(BusinessException.class, exception ->
                assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.AI_PROMPT_NOT_FOUND)
        );

        verify(resumeRepository).findByIdAndUserId(1L, 1L);
        verify(aiPromptRepository).findActiveByType(AiPromptType.RESUME_REVIEW);
        // 프롬프트 조회에서 실패했으므로 AI 호출, 평가 저장, 이벤트 발행은 실행되면 안됨
        verifyNoInteractions(resumeAiReviewPort);
        verifyNoInteractions(resumeEvaluationRepository);
        verifyNoInteractions(eventPublisher);
    }
}