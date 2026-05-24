package com.sashimi.resume.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI_RESUME_EVALUATIONS 테이블에 대응되는 도메인 모델.
 *
 * 이력서 AI 평가 결과는 단순 API 응답이 아니라,
 * 사용자가 나중에 다시 조회할 수 있는 저장 대상이다.
 */
public class ResumeEvaluation {

    private final Long evaluationId;
    private final BigDecimal overallScore;
    private final String strengths;
    private final String weaknesses;
    private final String suggestions;
    private final String aiResult;
    private final LocalDateTime evaluationAt;
    private final Long resumeId;
    private final Long jobPostingId;
    private final Long promptId;

    private ResumeEvaluation(
            Long evaluationId,
            BigDecimal overallScore,
            String strengths,
            String weaknesses,
            String suggestions,
            String aiResult,
            LocalDateTime evaluationAt,
            Long resumeId,
            Long jobPostingId,
            Long promptId
    ) {
        this.evaluationId = evaluationId;
        this.overallScore = overallScore;
        this.strengths = strengths;
        this.weaknesses = weaknesses;
        this.suggestions = suggestions;
        this.aiResult = aiResult;
        this.evaluationAt = evaluationAt;
        this.resumeId = resumeId;
        this.jobPostingId = jobPostingId;
        this.promptId = promptId;
    }

    /**
     * AI 평가가 성공했을 때만 평가 결과 객체를 생성한다.
     */
    public static ResumeEvaluation evaluated(
            BigDecimal overallScore,
            String strengths,
            String weaknesses,
            String suggestions,
            String aiResult,
            Long resumeId,
            Long jobPostingId,
            Long promptId
    ) {
        return new ResumeEvaluation(
                null,
                overallScore,
                strengths,
                weaknesses,
                suggestions,
                aiResult,
                LocalDateTime.now(),
                resumeId,
                jobPostingId,
                promptId
        );
    }

    public Long evaluationId() {
        return evaluationId;
    }

    public BigDecimal overallScore() {
        return overallScore;
    }

    public String strengths() {
        return strengths;
    }

    public String weaknesses() {
        return weaknesses;
    }

    public String suggestions() {
        return suggestions;
    }

    public String aiResult() {
        return aiResult;
    }

    public LocalDateTime evaluationAt() {
        return evaluationAt;
    }

    public Long resumeId() {
        return resumeId;
    }

    public Long jobPostingId() {
        return jobPostingId;
    }

    public Long promptId() {
        return promptId;
    }

    public ResumeEvaluation withId(Long evaluationId) {
        return new ResumeEvaluation(
                evaluationId,
                this.overallScore,
                this.strengths,
                this.weaknesses,
                this.suggestions,
                this.aiResult,
                this.evaluationAt,
                this.resumeId,
                this.jobPostingId,
                this.promptId
        );
    }

}
