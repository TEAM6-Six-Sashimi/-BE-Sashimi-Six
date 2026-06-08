package com.sashimi.verification.domain.model;

import java.time.LocalDateTime;

public class EmailVerification {

    private Long id;
    private String targetEmail;
    private String code;
    private VerificationPurpose purpose;
    private boolean verified;
    private LocalDateTime expiredAt;
    private LocalDateTime verifiedAt;
    private LocalDateTime createdAt;
    private Long userId;

    public EmailVerification(
            Long id,
            String targetEmail,
            String code,
            VerificationPurpose purpose,
            boolean verified,
            LocalDateTime expiredAt,
            LocalDateTime verifiedAt,
            LocalDateTime createdAt,
            Long userId
    ) {
        this.id = id;
        this.targetEmail = targetEmail;
        this.code = code;
        this.purpose = purpose;
        this.verified = verified;
        this.expiredAt = expiredAt;
        this.verifiedAt = verifiedAt;
        this.createdAt = createdAt;
        this.userId = userId;
    }

    public static EmailVerification create(
            String targetEmail,
            String code,
            VerificationPurpose purpose,
            LocalDateTime createdAt,
            LocalDateTime expiredAt,
            Long userId
    ) {
        return new EmailVerification(
                null,
                targetEmail,
                code,
                purpose,
                false,
                expiredAt,
                null,
                createdAt,
                userId
        );
    }

    public boolean isExpired(LocalDateTime now) {
        return expiredAt.isBefore(now);
    }

    public boolean isMatched(String inputCode) {
        return code.equalsIgnoreCase(inputCode);
    }

    public void verify(LocalDateTime now) {
        this.verified = true;
        this.verifiedAt = now;
    }

    public Long getId() { return id; }
    public String getTargetEmail() { return targetEmail; }
    public String getCode() { return code; }
    public VerificationPurpose getPurpose() { return purpose; }
    public boolean isVerified() { return verified; }
    public LocalDateTime getExpiredAt() { return expiredAt; }
    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Long getUserId() { return userId; }
}