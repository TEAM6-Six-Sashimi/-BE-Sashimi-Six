package com.sashimi.verification.application.policy;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.verification.domain.model.EmailVerification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EmailVerificationPolicy {

    private static final long RESEND_INTERVAL_SECONDS = 60;

    public long resendIntervalSeconds() {
        return RESEND_INTERVAL_SECONDS;
    }

    public void validateResendAvailable(EmailVerification latest, LocalDateTime now) {
        LocalDateTime createdAt = latest.getCreatedAt();

        if (createdAt != null
                && createdAt.plusSeconds(RESEND_INTERVAL_SECONDS).isAfter(now)) {
            throw new BusinessException(ErrorCode.EMAIL_VERIFICATION_RESEND_TOO_SOON);
        }
    }

    public void validateConfirmable(
            EmailVerification emailVerification,
            String inputCode,
            LocalDateTime now
    ) {
        if (emailVerification.isExpired(now)) {
            throw new BusinessException(ErrorCode.EMAIL_VERIFICATION_EXPIRED);
        }

        if (!emailVerification.isMatched(inputCode)) {
            throw new BusinessException(ErrorCode.INVALID_EMAIL_VERIFICATION_CODE);
        }
    }

    public void validateVerified(EmailVerification emailVerification, LocalDateTime now) {
        if (!emailVerification.isVerified()) {
            throw new BusinessException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        if (emailVerification.isExpired(now)) {
            throw new BusinessException(ErrorCode.EMAIL_VERIFICATION_EXPIRED);
        }
    }
}
