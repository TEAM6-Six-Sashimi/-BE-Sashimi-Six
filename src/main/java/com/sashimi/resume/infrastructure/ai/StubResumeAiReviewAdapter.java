package com.sashimi.resume.infrastructure.ai;

import com.sashimi.ai.domain.model.AiPrompt;
import com.sashimi.resume.application.port.ResumeAiReviewPort;
import com.sashimi.resume.application.port.ResumeAiReviewResult;
import com.sashimi.resume.domain.model.Resume;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * OpenAI 연동 전까지 사용하는 임시 Adapter.
 *
 * 실제 OpenAI 호출 없이 고정된 평가 결과를 반환해서
 * Controller, Service, Repository 흐름을 먼저 검증할 수 있다.
 */
@Component
@Profile({"local", "gemini"})
public class StubResumeAiReviewAdapter implements ResumeAiReviewPort {

    @Override
    public ResumeAiReviewResult review(Resume resume, AiPrompt prompt) {
        return new ResumeAiReviewResult(
                BigDecimal.valueOf(82),
                "Java/Spring 기반 프로젝트 경험이 강점입니다.",
                "클라우드 배포와 운영 경험이 부족합니다.",
                "AWS 배포 경험과 Redis 캐싱 프로젝트를 보완하면 좋습니다.",
                """
                {
                  "summary": "백엔드 개발자로서 기본 역량은 좋지만 운영 경험 보완이 필요합니다.",
                  "recommendedActions": [
                    "AWS 배포 실습 추가",
                    "Redis 캐싱 프로젝트 경험 추가"
                  ]
                }
                """
        );
    }
}
