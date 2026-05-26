package com.sashimi.auth.application.policy;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.user.domain.model.Role;
import com.sashimi.user.domain.model.User;
import com.sashimi.user.domain.model.UserStatus;
import com.sashimi.user.domain.repository.UserRepository;
import com.sashimi.user.dto.SignupRequestDto;
import com.sashimi.verification.application.usecase.EmailVerificationUseCase;
import com.sashimi.verification.domain.model.VerificationPurpose;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.*;

class SignupEligibilityPolicyTest {

    private UserRepository userRepository;
    private EmailVerificationUseCase emailVerificationUseCase;
    private SignupEligibilityPolicy signupEligibilityPolicy;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        emailVerificationUseCase = mock(EmailVerificationUseCase.class);

        signupEligibilityPolicy = new SignupEligibilityPolicy(
                userRepository,
                emailVerificationUseCase
        );
    }

    @Test
    void validateThrowsWhenReferralCodeIsInvalid() {
        // given
        SignupRequestDto request = signupRequest("wrong-code");

        when(userRepository.findByReferralCode("WRONG-CODE"))
                .thenReturn(Optional.empty());

        // when
        BusinessException exception = catchThrowableOfType(
                () -> signupEligibilityPolicy.validate(request),
                BusinessException.class
        );

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_REFERRAL_CODE);
        verify(emailVerificationUseCase).validateVerifiedEmail(
                "test@example.com",
                VerificationPurpose.SIGNUP
        );
        verify(userRepository).findByReferralCode("WRONG-CODE");
    }

    @Test
    void validateReturnsReferrerWhenReferralCodeIsValid() {
        // given
        SignupRequestDto request = signupRequest(" ref12345 ");
        User referrer = activeReferrer();

        when(userRepository.findByReferralCode("REF12345"))
                .thenReturn(Optional.of(referrer));

        // when
        SignupEligibility eligibility = signupEligibilityPolicy.validate(request);

        // then
        assertThat(eligibility.hasReferrer()).isTrue();
        assertThat(eligibility.referrer()).isEqualTo(referrer);

        verify(userRepository).findByReferralCode("REF12345");
    }

    @Test
    void validateThrowsWhenEmailIsNotVerified() {
        // given
        SignupRequestDto request = signupRequest(null);

        doThrow(new BusinessException(ErrorCode.EMAIL_NOT_VERIFIED))
                .when(emailVerificationUseCase)
                .validateVerifiedEmail("test@example.com", VerificationPurpose.SIGNUP);

        // when
        BusinessException exception = catchThrowableOfType(
                () -> signupEligibilityPolicy.validate(request),
                BusinessException.class
        );

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EMAIL_NOT_VERIFIED);
        verify(userRepository, never()).findByReferralCode(any());
    }

    private SignupRequestDto signupRequest(String referralCode) {
        SignupRequestDto request = new SignupRequestDto();

        ReflectionTestUtils.setField(request, "loginId", "testuser1");
        ReflectionTestUtils.setField(request, "password", "Password1!");
        ReflectionTestUtils.setField(request, "passwordConfirm", "Password1!");
        ReflectionTestUtils.setField(request, "email", "test@example.com");
        ReflectionTestUtils.setField(request, "name", "테스트회원");
        ReflectionTestUtils.setField(request, "referralCode", referralCode);

        return request;
    }

    private User activeReferrer() {
        return new User(
                2L,
                "추천인",
                "referrer1",
                "encodedPassword",
                "referrer@example.com",
                Role.STUDENT,
                UserStatus.ACTIVE,
                true,
                "REF12345",
                null
        );
    }
}