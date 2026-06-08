package com.sashimi.ai.domain.repository;

import com.sashimi.ai.domain.model.AiPrompt;
import com.sashimi.ai.domain.model.AiPromptType;

import java.util.Optional;

/**
 * AI 프롬프트 저장소 Port.
 *
 * domain/application 계층은 DB나 JPA를 직접 알지 않고,
 * 이 인터페이스를 통해 활성 프롬프트를 조회한다.
 */
public interface AiPromptRepository {

    /**
     * 특정 프롬프트 타입의 활성화된 프롬프트를 조회한다.
     *
     * 예:
     * RESUME_REVIEW 타입의 isActive = true 프롬프트 조회
     */
    Optional<AiPrompt> findActiveByType(AiPromptType promptType);
}
