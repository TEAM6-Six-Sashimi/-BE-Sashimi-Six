package com.sashimi.auth.application.policy;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.user.domain.model.User;
import com.sashimi.user.domain.repository.UserRepository;
import com.sashimi.user.dto.SignupRequestDto;
import com.sashimi.verification.application.usecase.EmailVerificationUseCase;
import com.sashimi.verification.domain.model.VerificationPurpose;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class SignupEligibilityPolicy {

    private final UserRepository userRepository;
    private final EmailVerificationUseCase emailVerificationUseCase;

    public SignupEligibility validate(SignupRequestDto request) {
        validateLoginIdNotDuplicated(request.getLoginId());
        validateEmailNotDuplicated(request.getEmail());
        validateSignupEmailVerified(request.getEmail());

        User referrer = findValidReferrer(request.getReferralCode());

        return new SignupEligibility(referrer);
    }

    private void validateLoginIdNotDuplicated(String loginId) {
        if (userRepository.existsByLoginId(loginId)) {
            throw new BusinessException(ErrorCode.DUPLICATE_LOGIN_ID);
        }
    }

    private void validateEmailNotDuplicated(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
    }

    private void validateSignupEmailVerified(String email) {
        emailVerificationUseCase.validateVerifiedEmail(
                email,
                VerificationPurpose.SIGNUP
        );
    }

    private User findValidReferrer(String referralCode) {
        String normalizedReferralCode = normalizeReferralCode(referralCode);

        if (normalizedReferralCode == null) {
            return null;
        }

        return userRepository.findByReferralCode(normalizedReferralCode)
                .filter(User::isActive)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFERRAL_CODE));
    }

    private String normalizeReferralCode(String referralCode) {
        if (referralCode == null || referralCode.isBlank()) {
            return null;
        }

        return referralCode.trim().toUpperCase(Locale.ROOT);
    }
}
