package com.sashimi.ai.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataAiPromptRepository extends JpaRepository<AiPromptJpaEntity, Long> {

    Optional<AiPromptJpaEntity> findFirstByPurposeAndActiveTrueOrderByVersionDesc(String purpose);
}