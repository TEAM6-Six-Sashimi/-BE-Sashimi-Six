package com.sashimi.resume.application.service;


import com.sashimi.ai.domain.model.AiPrompt;
import com.sashimi.ai.domain.model.AiPromptType;
import com.sashimi.ai.domain.repository.AiPromptRepository;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.resume.application.command.CreateResumeCommand;
import com.sashimi.resume.application.command.DeleteResumeCommand;
import com.sashimi.resume.application.command.ReviewResumeCommand;
import com.sashimi.resume.application.command.UpdateResumeCommand;
import com.sashimi.resume.application.event.ResumeEvaluatedEvent;
import com.sashimi.resume.application.port.ResumeAiReviewPort;
import com.sashimi.resume.application.port.ResumeAiReviewResult;
import com.sashimi.resume.application.usecase.ResumeCommandUseCase;
import com.sashimi.resume.application.usecase.ReviewResumeUseCase;
import com.sashimi.resume.domain.model.Resume;
import com.sashimi.resume.domain.model.ResumeEvaluation;
import com.sashimi.resume.domain.repository.ResumeEvaluationRepository;
import com.sashimi.resume.domain.repository.ResumeRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResumeCommandService implements ResumeCommandUseCase, ReviewResumeUseCase {

    private final ResumeRepository resumeRepository;
    private final ResumeEvaluationRepository resumeEvaluationRepository;
    private final AiPromptRepository aiPromptRepository;
    private final ResumeAiReviewPort resumeAiReviewPort;
    private final ApplicationEventPublisher eventPublisher;

    public ResumeCommandService(
            ResumeRepository resumeRepository,
            ResumeEvaluationRepository resumeEvaluationRepository,
            AiPromptRepository aiPromptRepository,
            ResumeAiReviewPort resumeAiReviewPort,
            ApplicationEventPublisher eventPublisher
    ) {
        this.resumeRepository = resumeRepository;
        this.resumeEvaluationRepository = resumeEvaluationRepository;
        this.aiPromptRepository = aiPromptRepository;
        this.resumeAiReviewPort = resumeAiReviewPort;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public ResumeEvaluation review(ReviewResumeCommand command) {
        // 1. 요청한 사용자의 이력서인지 확인하면서 조회한다.
        Resume resume = resumeRepository.findByIdAndUserId(command.resumeId(), command.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESUME_NOT_FOUND));

        // 2. 이력서 평가에 사용할 활성 프롬프트를 조회한다.
        AiPrompt prompt = aiPromptRepository.findActiveByType(AiPromptType.RESUME_REVIEW)
                .orElseThrow(() -> new BusinessException(ErrorCode.AI_PROMPT_NOT_FOUND));

        // 3. 실제 OpenAI 호출은 Port 뒤의 Infrastructure Adapter가 담당한다.
        ResumeAiReviewResult aiResult = resumeAiReviewPort.review(resume, prompt);

        // 4. 외부 AI 응답을 저장 가능한 도메인 모델로 변환한다.
        ResumeEvaluation evaluation = ResumeEvaluation.evaluated(
                aiResult.overallScore(),
                aiResult.strengths(),
                aiResult.weaknesses(),
                aiResult.suggestions(),
                aiResult.aiResult(),
                resume.resumeId(),
                command.jobPostingId(),
                prompt.promptId()
        );

        ResumeEvaluation savedEvaluation = resumeEvaluationRepository.save(evaluation);

        eventPublisher.publishEvent(
                new ResumeEvaluatedEvent(
                        command.userId(),
                        resume.resumeId(),
                        savedEvaluation.evaluationId(),
                        command.jobPostingId(),
                        savedEvaluation.overallScore(),
                        savedEvaluation.evaluationAt()
                )
        );

        return savedEvaluation;
    }

    @Override
    @Transactional
    public Resume create(CreateResumeCommand command) {
        Resume resume = Resume.create(
                command.userId(),
                command.title(),
                command.templateType(),
                command.content(),
                command.defaultResume()
        );

        return resumeRepository.save(resume);
    }

    @Override
    @Transactional
    public Resume update(UpdateResumeCommand command) {
        Resume resume = resumeRepository.findByIdAndUserId(command.resumeId(), command.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESUME_NOT_FOUND));

        Resume updatedResume = resume.update(
                command.title(),
                command.templateType(),
                command.content(),
                command.defaultResume()
        );

        return resumeRepository.save(updatedResume);
    }

    @Override
    @Transactional
    public void delete(DeleteResumeCommand command) {
        Resume resume = resumeRepository.findByIdAndUserId(command.resumeId(), command.userId())
                .orElseThrow(() -> new IllegalArgumentException("Resume not found."));

        resumeRepository.delete(resume);
    }

}