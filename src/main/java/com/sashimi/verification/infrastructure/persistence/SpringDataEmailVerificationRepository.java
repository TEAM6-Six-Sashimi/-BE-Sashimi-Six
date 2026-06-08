package com.sashimi.verification.infrastructure.persistence;

import com.sashimi.verification.domain.model.VerificationPurpose;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataEmailVerificationRepository
        extends JpaRepository<EmailVerificationJpaEntity, Long> {

    Optional<EmailVerificationJpaEntity> findFirstByTargetEmailAndPurposeOrderByCreatedAtDesc(
            String targetEmail,
            VerificationPurpose purpose
    );

    boolean existsByTargetEmailAndPurposeAndVerifiedTrue(
            String targetEmail,
            VerificationPurpose purpose
    );

    void deleteAllByUserId(Long userId);
}