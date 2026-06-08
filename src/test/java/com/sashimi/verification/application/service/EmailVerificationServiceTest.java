package com.sashimi.verification.application.service;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.verification.application.command.ConfirmEmailVerificationCommand;
import com.sashimi.verification.application.command.RequestEmailVerificationCommand;
import com.sashimi.verification.application.policy.EmailVerificationPolicy;
import com.sashimi.verification.application.port.EmailSender;
import com.sashimi.verification.domain.model.EmailVerification;
import com.sashimi.verification.domain.model.VerificationPurpose;
import com.sashimi.verification.domain.repository.EmailVerificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.*;

class EmailVerificationServiceTest {

    private EmailVerificationRepository emailVerificationRepository;
    private VerificationCodeGenerator verificationCodeGenerator;
    private EmailSender emailSender;
    private EmailVerificationService emailVerificationService;

    @BeforeEach
    void setUp() {
        emailVerificationRepository = mock(EmailVerificationRepository.class);
        verificationCodeGenerator = mock(VerificationCodeGenerator.class);
        emailSender = mock(EmailSender.class);

        EmailVerificationPolicy emailVerificationPolicy = new EmailVerificationPolicy();

        emailVerificationService = new EmailVerificationService(
                emailVerificationRepository,
                verificationCodeGenerator,
                emailSender,
                emailVerificationPolicy
        );
    }

    @Test
    void validateVerifiedEmailThrowsWhenVerificationNotFound() {
        // given
        when(emailVerificationRepository.findLatestByTargetEmailAndPurpose(
                "test@example.com",
                VerificationPurpose.SIGNUP
        )).thenReturn(Optional.empty());

        // when
        BusinessException exception = catchThrowableOfType(
                () -> emailVerificationService.validateVerifiedEmail(
                        "test@example.com",
                        VerificationPurpose.SIGNUP
                ),
                BusinessException.class
        );

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EMAIL_NOT_VERIFIED);
        verify(emailVerificationRepository).findLatestByTargetEmailAndPurpose(
                "test@example.com",
                VerificationPurpose.SIGNUP
        );
    }

    @Test
    void confirmEmailVerificationThrowsWhenCodeDoesNotMatch() {
        // given
        EmailVerification emailVerification = EmailVerification.create(
                "test@example.com",
                "ABC12345",
                VerificationPurpose.SIGNUP,
                LocalDateTime.now().minusMinutes(1),
                LocalDateTime.now().plusMinutes(9),
                null
        );

        when(emailVerificationRepository.findLatestByTargetEmailAndPurpose(
                "test@example.com",
                VerificationPurpose.SIGNUP
        )).thenReturn(Optional.of(emailVerification));

        // when
        BusinessException exception = catchThrowableOfType(
                () -> emailVerificationService.confirmEmailVerification(
                        new ConfirmEmailVerificationCommand(
                                "test@example.com",
                                VerificationPurpose.SIGNUP,
                                "ZZZ99999"
                        )
                ),
                BusinessException.class
        );

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_EMAIL_VERIFICATION_CODE);
        verify(emailVerificationRepository, never()).save(any());
    }

    @Test
    void requestEmailVerificationThrowsWhenResendTooSoon() {
        // given
        EmailVerification latest = EmailVerification.create(
                "test@example.com",
                "ABC12345",
                VerificationPurpose.SIGNUP,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10),
                null
        );

        when(emailVerificationRepository.findLatestByTargetEmailAndPurpose(
                "test@example.com",
                VerificationPurpose.SIGNUP
        )).thenReturn(Optional.of(latest));

        // when
        BusinessException exception = catchThrowableOfType(
                () -> emailVerificationService.requestEmailVerification(
                        new RequestEmailVerificationCommand(
                                "test@example.com",
                                VerificationPurpose.SIGNUP,
                                null
                        )
                ),
                BusinessException.class
        );

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EMAIL_VERIFICATION_RESEND_TOO_SOON);
        verifyNoInteractions(verificationCodeGenerator);
        verifyNoInteractions(emailSender);
    }
}