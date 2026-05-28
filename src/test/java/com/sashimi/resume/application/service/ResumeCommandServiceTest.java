// TODO : 수정 예정
//package com.sashimi.resume.application.service;
//
//import com.sashimi.ai.domain.model.AiPrompt;
//import com.sashimi.ai.domain.model.AiPromptType;
//import com.sashimi.ai.domain.repository.AiPromptRepository;
//import com.sashimi.resume.application.command.CreateResumeCommand;
//import com.sashimi.resume.application.command.ReviewResumeCommand;
//import com.sashimi.resume.application.port.ResumeAiReviewPort;
//import com.sashimi.resume.application.port.ResumeAiReviewResult;
//import com.sashimi.resume.domain.model.Resume;
//import com.sashimi.resume.domain.model.ResumeEvaluation;
//import com.sashimi.resume.domain.model.ResumeReviewSection;
//import com.sashimi.resume.domain.repository.ResumeEvaluationRepository;
//import com.sashimi.resume.domain.repository.ResumeRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.math.BigDecimal;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class ResumeCommandServiceTest {
//
//    private ResumeRepository resumeRepository;
//    private ResumeEvaluationRepository resumeEvaluationRepository;
//    private AiPromptRepository aiPromptRepository;
//    private ResumeAiReviewPort resumeAiReviewPort;
//
//    private ResumeCommandService resumeCommandService;
//
//    @BeforeEach
//    void setUp() {
//        resumeRepository = mock(ResumeRepository.class);
//        resumeEvaluationRepository = mock(ResumeEvaluationRepository.class);
//        aiPromptRepository = mock(AiPromptRepository.class);
//        resumeAiReviewPort = mock(ResumeAiReviewPort.class);
//
//        resumeCommandService = new ResumeCommandService(
//                resumeRepository,
//                resumeEvaluationRepository,
//                aiPromptRepository,
//                resumeAiReviewPort
//        );
//    }
//
//    @Test
//    void createResume() {
//        // given
//        CreateResumeCommand command = new CreateResumeCommand(
//                1L,
//                "Backend Resume",
//                "{\"summary\":\"Java backend developer\"}",
//                true
//        );
//
//        Resume savedResume = Resume.create(
//                1L,
//                "Backend Resume",
//                ResumeReviewSection.BASIC,
//                "{\"summary\":\"Java backend developer\"}",
//                true
//        ).withId(10L);
//
//        when(resumeRepository.save(any(Resume.class))).thenReturn(savedResume);
//
//        // when
//        Resume result = resumeCommandService.create(command);
//
//        // then
//        assertThat(result.resumeId()).isEqualTo(10L);
//        assertThat(result.userId()).isEqualTo(1L);
//        assertThat(result.title()).isEqualTo("Backend Resume");
//        assertThat(result.defaultResume()).isTrue();
//
//        verify(resumeRepository).save(any(Resume.class));
//    }
//
//    @Test
//    void reviewResume() {
//        // given
//        Resume resume = new Resume(
//                1L,
//                1L,
//                "Backend Resume",
//                "{\"summary\":\"Java backend developer\"}"
//        );
//
//        AiPrompt prompt = new AiPrompt(
//                1L,
//                "Resume Review Prompt",
//                AiPromptType.RESUME_REVIEW,
//                "Review this resume: {resumeContent}",
//                1,
//                true
//        );
//
//        ResumeAiReviewResult aiResult = new ResumeAiReviewResult(
//                BigDecimal.valueOf(82),
//                "Strong Java/Spring experience",
//                "Needs cloud experience",
//                "Add AWS deployment project",
//                "{\"summary\":\"Good backend resume\"}"
//        );
//
//        ResumeEvaluation savedEvaluation = ResumeEvaluation.evaluated(
//                aiResult.overallScore(),
//                aiResult.strengths(),
//                aiResult.weaknesses(),
//                aiResult.suggestions(),
//                aiResult.aiResult(),
//                1L,
//                null,
//                1L
//        ).withId(100L);
//
//        when(resumeRepository.findByIdAndUserId(1L, 1L))
//                .thenReturn(Optional.of(resume));
//        when(aiPromptRepository.findActiveByType(AiPromptType.RESUME_REVIEW))
//                .thenReturn(Optional.of(prompt));
//        when(resumeAiReviewPort.review(resume, prompt))
//                .thenReturn(aiResult);
//        when(resumeEvaluationRepository.save(any(ResumeEvaluation.class)))
//                .thenReturn(savedEvaluation);
//
//        // when
//        ResumeEvaluation result = resumeCommandService.review(
//                new ReviewResumeCommand(1L, 1L, null)
//        );
//
//        // then
//        assertThat(result.evaluationId()).isEqualTo(100L);
//        assertThat(result.overallScore()).isEqualByComparingTo("82");
//        assertThat(result.strengths()).isEqualTo("Strong Java/Spring experience");
//
//        verify(resumeRepository).findByIdAndUserId(1L, 1L);
//        verify(aiPromptRepository).findActiveByType(AiPromptType.RESUME_REVIEW);
//        verify(resumeAiReviewPort).review(resume, prompt);
//        verify(resumeEvaluationRepository).save(any(ResumeEvaluation.class));
//    }
//
//    @Test
//    void reviewResumeThrowsWhenResumeNotFound() {
//        // given
//        when(resumeRepository.findByIdAndUserId(999L, 1L))
//                .thenReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() ->
//                resumeCommandService.review(new ReviewResumeCommand(1L, 999L, null))
//        ).isInstanceOf(IllegalArgumentException.class);
//
//        verify(resumeRepository).findByIdAndUserId(999L, 1L);
//        verifyNoInteractions(aiPromptRepository);
//        verifyNoInteractions(resumeAiReviewPort);
//        verifyNoInteractions(resumeEvaluationRepository);
//    }
//
//    @Test
//    void reviewResumeThrowsWhenActivePromptNotFound() {
//        // given
//        Resume resume = new Resume(
//                1L,
//                1L,
//                "Backend Resume",
//                "{\"summary\":\"Java backend developer\"}"
//        );
//
//        when(resumeRepository.findByIdAndUserId(1L, 1L))
//                .thenReturn(Optional.of(resume));
//        when(aiPromptRepository.findActiveByType(AiPromptType.RESUME_REVIEW))
//                .thenReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() ->
//                resumeCommandService.review(new ReviewResumeCommand(1L, 1L, null))
//        ).isInstanceOf(IllegalStateException.class);
//
//        verify(resumeRepository).findByIdAndUserId(1L, 1L);
//        verify(aiPromptRepository).findActiveByType(AiPromptType.RESUME_REVIEW);
//        verifyNoInteractions(resumeAiReviewPort);
//        verifyNoInteractions(resumeEvaluationRepository);
//    }
//}
