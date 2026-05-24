package com.sashimi.resume.application.port;

import com.sashimi.ai.domain.model.AiPrompt;
import com.sashimi.resume.domain.model.Resume;

/**
 * 이력서 AI 평가를 위한 외부 시스템 호출 Port.
 *
 * application 계층은 OpenAI SDK를 직접 알지 않고,
 * 이 인터페이스를 통해서만 AI 평가 기능을 사용한다.
 */
public interface ResumeAiReviewPort {

    ResumeAiReviewResult review(Resume resume, AiPrompt prompt);
}
