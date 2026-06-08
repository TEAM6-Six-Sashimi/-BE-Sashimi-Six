package com.sashimi.verification.infrastructure.persistence;

import com.sashimi.verification.domain.model.EmailVerification;
import com.sashimi.verification.domain.model.VerificationPurpose;
import com.sashimi.verification.domain.repository.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EmailVerificationRepositoryAdapter implements EmailVerificationRepository {

    private final SpringDataEmailVerificationRepository springDataEmailVerificationRepository;

    @Override
    public EmailVerification save(EmailVerification emailVerification) {
        return springDataEmailVerificationRepository.save(
                EmailVerificationJpaEntity.from(emailVerification)
        ).toDomain();
    }

    @Override
    public Optional<EmailVerification> findLatestByTargetEmailAndPurpose(
            String targetEmail,
            VerificationPurpose purpose
    ) {
        return springDataEmailVerificationRepository
                .findFirstByTargetEmailAndPurposeOrderByCreatedAtDesc(targetEmail, purpose)
                .map(EmailVerificationJpaEntity::toDomain);
    }

    @Override
    public boolean existsVerifiedByTargetEmailAndPurpose(
            String targetEmail,
            VerificationPurpose purpose
    ) {
        return springDataEmailVerificationRepository
                .existsByTargetEmailAndPurposeAndVerifiedTrue(targetEmail, purpose);
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        springDataEmailVerificationRepository.deleteAllByUserId(userId);
    }
}
