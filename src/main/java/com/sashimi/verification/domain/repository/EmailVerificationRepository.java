package com.sashimi.verification.domain.repository;

import com.sashimi.verification.domain.model.EmailVerification;
import com.sashimi.verification.domain.model.VerificationPurpose;

import java.util.Optional;

public interface EmailVerificationRepository {

    EmailVerification save(EmailVerification emailVerification);

    Optional<EmailVerification> findLatestByTargetEmailAndPurpose(
            String targetEmail,
            VerificationPurpose purpose
    );

    boolean existsVerifiedByTargetEmailAndPurpose(
            String targetEmail,
            VerificationPurpose purpose
    );
}
