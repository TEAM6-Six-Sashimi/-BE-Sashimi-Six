package com.sashimi.resume.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataResumeRepository
        extends JpaRepository<ResumeJpaEntity, Long> {

    Optional<ResumeJpaEntity> findByResumeIdAndUserId(
            Long resumeId,
            Long userId
    );

    List<ResumeJpaEntity> findAllByUserIdOrderByCreatedAtDesc(
            Long userId
    );

    void deleteByResumeIdAndUserId(
            Long resumeId,
            Long userId
    );
}