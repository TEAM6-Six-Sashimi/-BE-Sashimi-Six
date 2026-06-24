package com.sashimi.verification.application.service;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.verification.application.command.ConfirmEmailVerificationCommand;
import com.sashimi.verification.application.command.RequestEmailVerificationCommand;
import com.sashimi.verification.application.port.EmailSender;
import com.sashimi.verification.presentation.api.response.EmailVerificationConfirmResult;
import com.sashimi.verification.presentation.api.response.EmailVerificationRequestResult;
import com.sashimi.verification.application.usecase.EmailVerificationUseCase;
import com.sashimi.verification.domain.model.EmailVerification;
import com.sashimi.verification.domain.model.VerificationPurpose;
import com.sashimi.verification.domain.repository.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sashimi.verification.application.policy.EmailVerificationPolicy;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailVerificationService implements EmailVerificationUseCase {

    private static final long EXPIRE_MINUTES = 10;


    private final EmailVerificationRepository emailVerificationRepository;
    private final VerificationCodeGenerator verificationCodeGenerator;
    private final EmailSender emailSender;
    private final EmailVerificationPolicy emailVerificationPolicy;

    @Override
    public EmailVerificationRequestResult requestEmailVerification(RequestEmailVerificationCommand command) {
        String targetEmail = normalizeEmail(command.getTargetEmail());
        LocalDateTime now = LocalDateTime.now();

        emailVerificationRepository.findLatestByTargetEmailAndPurpose(
                targetEmail,
                command.getPurpose()
        ).ifPresent(latest -> emailVerificationPolicy.validateResendAvailable(latest, now));

        String code = verificationCodeGenerator.generate();

        EmailVerification emailVerification = EmailVerification.create(
                targetEmail,
                code,
                command.getPurpose(),
                now,
                now.plusMinutes(EXPIRE_MINUTES),
                command.getUserId()
        );

        emailVerificationRepository.save(emailVerification);

        emailSender.send(
                targetEmail,
                createSubject(command.getPurpose()),
                createContent(code)
        );

        return new EmailVerificationRequestResult(
                targetEmail,
                command.getPurpose(),
                EXPIRE_MINUTES * 60,
                emailVerificationPolicy.resendIntervalSeconds());
    }

    @Override
    public EmailVerificationConfirmResult confirmEmailVerification(ConfirmEmailVerificationCommand command) {
        String targetEmail = normalizeEmail(command.getTargetEmail());
        LocalDateTime now = LocalDateTime.now();

        EmailVerification emailVerification = emailVerificationRepository
                .findLatestByTargetEmailAndPurpose(targetEmail, command.getPurpose())
                .orElseThrow(() -> new BusinessException(ErrorCode.EMAIL_VERIFICATION_NOT_FOUND));

        emailVerificationPolicy.validateConfirmable(
                emailVerification,
                command.getCode(),
                now
        );

        if (!emailVerification.isVerified()) {
            emailVerification.verify(now);
            emailVerificationRepository.save(emailVerification);
        }

        return new EmailVerificationConfirmResult(
                targetEmail,
                command.getPurpose(),
                true
        );
    }

    @Override
    @Transactional(readOnly = true)
    public void validateVerifiedEmail(String targetEmail, VerificationPurpose purpose) {
        String normalizedEmail = normalizeEmail(targetEmail);
        LocalDateTime now = LocalDateTime.now();

        EmailVerification emailVerification = emailVerificationRepository
                .findLatestByTargetEmailAndPurpose(normalizedEmail, purpose)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMAIL_NOT_VERIFIED));

        emailVerificationPolicy.validateVerified(emailVerification, now);
    }


    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String createSubject(VerificationPurpose purpose) {
        return switch (purpose) {
            case SIGNUP -> "[FitGyeok] 회원가입 이메일 인증 코드";
            case PASSWORD_RESET -> "[FitGyeok] 비밀번호 재설정 인증 코드";
        };
    }

    private String createContent(String code) {
        return "인증 코드: " + code + "\n"
                + "이 코드는 " + EXPIRE_MINUTES + "분 동안 유효합니다.";
    }
}