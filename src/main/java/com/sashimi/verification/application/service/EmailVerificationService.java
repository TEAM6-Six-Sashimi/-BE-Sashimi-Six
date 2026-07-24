package com.sashimi.verification.application.service;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.global.ratelimit.RateLimiterService;
import com.sashimi.verification.application.command.ConfirmEmailVerificationCommand;
import com.sashimi.verification.application.command.RequestEmailVerificationCommand;
import com.sashimi.verification.presentation.api.response.EmailVerificationConfirmResult;
import com.sashimi.verification.presentation.api.response.EmailVerificationRequestResult;
import com.sashimi.verification.application.usecase.EmailVerificationUseCase;
import com.sashimi.verification.domain.model.EmailVerification;
import com.sashimi.verification.domain.model.VerificationPurpose;
import com.sashimi.verification.domain.repository.EmailVerificationRepository;
import com.sashimi.verification.infrastructure.outbox.EmailOutboxJpaEntity;
import com.sashimi.verification.infrastructure.outbox.SpringDataEmailOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import com.sashimi.verification.application.policy.EmailVerificationPolicy;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailVerificationService implements EmailVerificationUseCase {

    private static final long EXPIRE_MINUTES = 10;
    private static final int HOURLY_REQUEST_LIMIT = 3;
    private static final int HOURLY_REQUEST_WINDOW_SECONDS = 3600;

    private static final String OUTBOX_QUEUE_KEY = "email_outbox_queue";

    private final EmailVerificationRepository emailVerificationRepository;
    private final VerificationCodeGenerator verificationCodeGenerator;
    private final SpringDataEmailOutboxRepository emailOutboxRepository;
    private final EmailVerificationPolicy emailVerificationPolicy;
    private final StringRedisTemplate redisTemplate;
    private final RateLimiterService rateLimiterService;

    @Override
    public EmailVerificationRequestResult requestEmailVerification(RequestEmailVerificationCommand command) {
        String targetEmail = normalizeEmail(command.getTargetEmail());
        LocalDateTime now = LocalDateTime.now();

        // 회원가입 인증 이메일은 60초 재발송 간격만으로는 시간당 총 발송량이 제한되지 않아
        // (비밀번호 재설정/아이디 찾기와 동일하게) 이메일당 시간당 3회로 제한한다.
        if (!rateLimiterService.isAllowed(
                "email-verification:" + command.getPurpose() + ":" + targetEmail,
                HOURLY_REQUEST_LIMIT,
                HOURLY_REQUEST_WINDOW_SECONDS
        )) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS);
        }

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

        EmailOutboxJpaEntity outbox = emailOutboxRepository.save(
                EmailOutboxJpaEntity.create(
                        targetEmail,
                        createSubject(command.getPurpose()),
                        createContent(code)
                )
        );

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                redisTemplate.opsForList().leftPush(OUTBOX_QUEUE_KEY, outbox.getId().toString());
            }
        });

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
            case FIND_ID -> "[FitGyeok] 아이디 찾기 인증 코드";
        };
    }

    private String createContent(String code) {
        return "인증 코드: " + code + "\n"
                + "이 코드는 " + EXPIRE_MINUTES + "분 동안 유효합니다.";
    }
}