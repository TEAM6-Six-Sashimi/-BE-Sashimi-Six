package com.sashimi.resume.domain.repository;

import com.sashimi.resume.domain.model.ResumeEvaluation;

/**
 * AI 이력서 평가 결과 저장소 Port.
 *
 * AI_RESUME_EVALUATIONS 테이블에 저장될 도메인 모델을 다룬다.
 */
public interface ResumeEvaluationRepository {

    /**
     * AI 평가 결과를 저장한다.
     */
    ResumeEvaluation save(ResumeEvaluation evaluation);
}
