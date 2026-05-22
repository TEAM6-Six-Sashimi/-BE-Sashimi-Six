package com.sashimi.resume.infrastructure.persistence;

import com.sashimi.resume.domain.model.ResumeEvaluation;
import com.sashimi.resume.domain.repository.ResumeEvaluationRepository;
import org.springframework.stereotype.Repository;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 개발 초기 단계에서 사용하는 임시 AI 이력서 평가 결과 저장소 Adapter.
 */
@Repository
public class InMemoryResumeEvaluationRepositoryAdapter implements ResumeEvaluationRepository {

    private final AtomicLong sequence = new AtomicLong(1);

    @Override
    public ResumeEvaluation save(ResumeEvaluation evaluation) {
        // 지금 ResumeEvaluation이 final 필드 기반이면 ID를 새로 넣기 어렵다.
        // 그래서 ResumeEvaluation에 withId() 또는 saved() 같은 메서드가 있으면 좋다.
        return evaluation.withId(sequence.getAndIncrement());
    }

}
