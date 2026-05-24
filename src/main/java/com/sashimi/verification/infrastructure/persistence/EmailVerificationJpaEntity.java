package com.sashimi.verification.infrastructure.persistence;

import com.sashimi.verification.domain.model.EmailVerification;
import com.sashimi.verification.domain.model.VerificationPurpose;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVerificationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verification_id")
    private Long id;

    @Column(name = "target_email", nullable = false)
    private String targetEmail;

    @Column(nullable = false, length = 20)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VerificationPurpose purpose;

    @Column(name = "is_verified", nullable = false)
    private boolean verified;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "user_id")
    private Long userId;

    public static EmailVerificationJpaEntity from(EmailVerification emailVerification) {
        EmailVerificationJpaEntity entity = new EmailVerificationJpaEntity();
        entity.id = emailVerification.getId();
        entity.targetEmail = emailVerification.getTargetEmail();
        entity.code = emailVerification.getCode();
        entity.purpose = emailVerification.getPurpose();
        entity.verified = emailVerification.isVerified();
        entity.expiredAt = emailVerification.getExpiredAt();
        entity.verifiedAt = emailVerification.getVerifiedAt();
        entity.createdAt = emailVerification.getCreatedAt();
        entity.userId = emailVerification.getUserId();
        return entity;
    }

    public EmailVerification toDomain() {
        return new EmailVerification(
                id,
                targetEmail,
                code,
                purpose,
                verified,
                expiredAt,
                verifiedAt,
                createdAt,
                userId
        );
    }
}