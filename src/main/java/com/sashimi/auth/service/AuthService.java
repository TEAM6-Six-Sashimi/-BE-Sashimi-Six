package com.sashimi.auth.service;

import com.sashimi.auth.application.policy.SignupEligibility;
import com.sashimi.auth.application.policy.SignupEligibilityPolicy;
import com.sashimi.global.ratelimit.RateLimiterService;
import com.sashimi.security.blacklist.TokenBlacklistService;
import com.sashimi.security.session.TokenVersionService;
import com.sashimi.auth.dto.FindLoginIdConfirmRequestDto;
import com.sashimi.auth.dto.FindLoginIdConfirmResponseDto;
import com.sashimi.auth.dto.FindLoginIdRequestDto;
import com.sashimi.auth.dto.FindLoginIdRequestResponseDto;
import com.sashimi.auth.dto.LoginRequestDto;
import com.sashimi.auth.dto.PasswordResetConfirmRequestDto;
import com.sashimi.auth.dto.PasswordResetConfirmResponseDto;
import com.sashimi.auth.dto.PasswordResetRequestDto;
import com.sashimi.auth.dto.PasswordResetRequestResponseDto;
import com.sashimi.auth.dto.TokenResponseDto;
import com.sashimi.category.domain.repository.CategoryRepository;
import com.sashimi.credit.application.command.CreateInitialCreditCommand;
import com.sashimi.credit.application.command.GrantReferralSignupRewardCommand;
import com.sashimi.credit.application.usecase.CreditCommandUseCase;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.security.jwt.JwtTokenProvider;
import com.sashimi.token.entity.RefreshToken;
import com.sashimi.token.service.RefreshService;
import com.sashimi.user.application.event.UserPasswordChangedEvent;
import com.sashimi.user.application.event.UserRegisteredEvent;
import com.sashimi.user.domain.model.User;
import com.sashimi.user.domain.repository.UserRepository;
import com.sashimi.user.dto.LoginIdCheckResponseDto;
import com.sashimi.user.dto.ReferralCodeCheckResponseDto;
import com.sashimi.user.dto.SignupRequestDto;
import com.sashimi.user.dto.UserResponseDto;
import com.sashimi.verification.application.command.ConfirmEmailVerificationCommand;
import com.sashimi.verification.application.command.RequestEmailVerificationCommand;
import com.sashimi.verification.application.usecase.EmailVerificationUseCase;
import com.sashimi.verification.domain.model.VerificationPurpose;
import com.sashimi.verification.presentation.api.response.EmailVerificationRequestResult;
import com.sashimi.auth.metric.AuthMetrics;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import com.sashimi.security.principal.CustomUserPrincipal;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final String REFERRAL_CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int REFERRAL_CODE_LENGTH = 8;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final AuthenticationManager authenticationManager;
    private final AuthMetrics authMetrics;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RefreshService refreshService;
    private final EmailVerificationUseCase emailVerificationUseCase;
    private final CreditCommandUseCase creditCommandUseCase;
    private final ApplicationEventPublisher eventPublisher;
    private final SignupEligibilityPolicy signupEligibilityPolicy;
    private final CategoryRepository categoryRepository;
    private final TokenBlacklistService tokenBlacklistService;
    private final TokenVersionService tokenVersionService;
    private final RateLimiterService rateLimiterService;

    public UserResponseDto register(SignupRequestDto request) {
        SignupEligibility eligibility = signupEligibilityPolicy.validate(request);
        List<Long> interestCategoryIds = normalizeInterestCategoryIds(request.getInterestCategoryIds());

        User user = User.createStudent(
                request.getName(),
                request.getLoginId(),
                passwordEncoder.encode(request.getPassword()),
                request.getEmail(),
                request.getPhone(),
                request.getBirthDate(),
                generateUniqueReferralCode(),
                interestCategoryIds,
                request.isMarketingConsent(),
                request.isEmailConsent(),
                request.isAiConsent()
        );

        User savedUser = userRepository.save(user);

        if (eligibility.hasReferrer()) {
            creditCommandUseCase.grantReferralSignupRewards(
                    new GrantReferralSignupRewardCommand(
                            savedUser.getId(),
                            eligibility.referrer().getId()
                    )
            );
        } else {
            creditCommandUseCase.createInitialCredit(
                    new CreateInitialCreditCommand(savedUser.getId(), 0L)
            );
        }

        eventPublisher.publishEvent(
                new UserRegisteredEvent(savedUser.getId(), savedUser.getName(), savedUser.getEmail())
        );

        return UserResponseDto.from(savedUser);
    }

    public PasswordResetRequestResponseDto requestPasswordReset(PasswordResetRequestDto request) {
        String email = normalizeEmail(request.getEmail());
        if (!rateLimiterService.isAllowed("password-reset:" + email, 3, 3600)) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!user.isActive()) {
            throw new BusinessException(ErrorCode.INACTIVE_USER);
        }

        EmailVerificationRequestResult result = emailVerificationUseCase.requestEmailVerification(
                new RequestEmailVerificationCommand(
                        email,
                        VerificationPurpose.PASSWORD_RESET,
                        user.getId()
                )
        );

        return new PasswordResetRequestResponseDto(
                result.targetEmail(),
                result.purpose(),
                result.expiresInSeconds(),
                result.resendAvailableInSeconds()
        );
    }

    public PasswordResetConfirmResponseDto resetPassword(PasswordResetConfirmRequestDto request) {
        String email = normalizeEmail(request.getEmail());

        emailVerificationUseCase.confirmEmailVerification(
                new ConfirmEmailVerificationCommand(
                        email,
                        VerificationPurpose.PASSWORD_RESET,
                        request.getCode()
                )
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!user.isActive()) {
            throw new BusinessException(ErrorCode.INACTIVE_USER);
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.SAME_AS_OLD_PASSWORD);
        }

        user.changePassword(passwordEncoder.encode(request.getNewPassword()));
        User savedUser = userRepository.save(user);

        refreshService.deleteByUser(savedUser);

        eventPublisher.publishEvent(
                new UserPasswordChangedEvent(
                        savedUser.getId(),
                        savedUser.getEmail(),
                        LocalDateTime.now()
                )
        );

        return new PasswordResetConfirmResponseDto(true, true);
    }

    public FindLoginIdRequestResponseDto requestFindLoginId(FindLoginIdRequestDto request) {
        String email = normalizeEmail(request.getEmail());
        if (!rateLimiterService.isAllowed("find-id:" + email, 3, 3600)) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS);
        }

        User user = findActiveUserByNameAndEmail(request.getName(), email);

        EmailVerificationRequestResult result = emailVerificationUseCase.requestEmailVerification(
                new RequestEmailVerificationCommand(
                        email,
                        VerificationPurpose.FIND_ID,
                        user.getId()
                )
        );

        return new FindLoginIdRequestResponseDto(
                result.targetEmail(),
                result.purpose(),
                result.expiresInSeconds(),
                result.resendAvailableInSeconds()
        );
    }

    public FindLoginIdConfirmResponseDto findLoginId(FindLoginIdConfirmRequestDto request) {
        String email = normalizeEmail(request.getEmail());

        emailVerificationUseCase.confirmEmailVerification(
                new ConfirmEmailVerificationCommand(
                        email,
                        VerificationPurpose.FIND_ID,
                        request.getCode()
                )
        );

        User user = findActiveUserByNameAndEmail(request.getName(), email);

        return new FindLoginIdConfirmResponseDto(user.getLoginId());
    }

    private User findActiveUserByNameAndEmail(String name, String email) {
        User user = userRepository.findByEmail(email)
                .filter(candidate -> candidate.getName().equals(name))
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!user.isActive()) {
            throw new BusinessException(ErrorCode.INACTIVE_USER);
        }

        return user;
    }

    public TokenResponseDto login(LoginRequestDto request) {
        if (!rateLimiterService.isAllowed("login:" + request.getLoginId(), 5, 300)) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS);
        }

        Timer.Sample timerSample = authMetrics.startTimer();

        try {
            Authentication authentication;
            try {
                authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getLoginId(),
                                request.getPassword()
                        )
                );
            } catch (AuthenticationException e) {
                authMetrics.recordLoginFailed(resolveFailureReason(e));
                log.warn("event=login_failed loginId={} reason={}", maskLoginId(request.getLoginId()), resolveFailureReason(e));
                throw e;
            }

            CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
            User user = userRepository.findById(principal.getId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

            user.updateLastLoginAt(LocalDateTime.now());
            userRepository.save(user);

            long version = tokenVersionService.incrementVersion(user.getId());
            TokenResponseDto tokenResponse = jwtTokenProvider.generateToken(authentication, user.getId(), version);

            LocalDateTime refreshExpiryDate = LocalDateTime.now()
                    .plusNanos(jwtTokenProvider.getRefreshTokenValidityInMilliseconds() * 1_000_000);

            refreshService.saveOrUpdate(user, tokenResponse.getRefreshToken(), refreshExpiryDate);

            authMetrics.recordLoginSuccess();
            log.info("event=login_success userId={}", user.getId());

            return tokenResponse.withName(user.getName());
        } finally {
            authMetrics.stopTimer(timerSample);
        }
    }

    private String resolveFailureReason(AuthenticationException e) {
        if (e instanceof BadCredentialsException) return "bad_credentials";
        if (e instanceof DisabledException) return "account_disabled";
        if (e instanceof LockedException) return "account_locked";
        return "unknown";
    }

    public TokenResponseDto reissue(String refreshTokenValue) {
        RefreshToken refreshToken = refreshService.findValidRefreshToken(refreshTokenValue);
        refreshToken.markUsed(LocalDateTime.now());

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        CustomUserPrincipal principal = CustomUserPrincipal.from(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal,
                "",
                principal.getAuthorities()
        );

        Long tokenVersion = jwtTokenProvider.extractVersion(refreshTokenValue);
        if (!tokenVersionService.isValidVersion(user.getId(), tokenVersion)) {
            throw new BusinessException(ErrorCode.CONCURRENT_SESSION_DETECTED);
        }

        TokenResponseDto tokenResponse = jwtTokenProvider.generateToken(authentication, user.getId(), tokenVersion);

        LocalDateTime refreshExpiryDate = LocalDateTime.now()
                .plusNanos(jwtTokenProvider.getRefreshTokenValidityInMilliseconds() * 1_000_000);

        refreshService.saveOrUpdate(user, tokenResponse.getRefreshToken(), refreshExpiryDate);

        return tokenResponse.withName(user.getName());
    }

    public void logout(String accessToken, String refreshTokenValue) {
        RefreshToken refreshToken = refreshService.findValidRefreshToken(refreshTokenValue);

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        refreshService.deleteByUser(user);

        if (accessToken != null) {
            long remainingMillis = jwtTokenProvider.getRemainingExpiry(accessToken);
            if (remainingMillis > 0) {
                tokenBlacklistService.blacklist(accessToken, remainingMillis);
            }
        }
    }

    @Transactional(readOnly = true)
    public LoginIdCheckResponseDto checkLoginId(String loginId) {
        if (!rateLimiterService.isAllowed("login-id-check:" + loginId, 10, 60)) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS);
        }
        return LoginIdCheckResponseDto.of(loginId, !userRepository.existsByLoginId(loginId));
    }

    @Transactional(readOnly = true)
    public ReferralCodeCheckResponseDto checkReferralCode(String referralCode) {
        String normalized = referralCode == null ? null : referralCode.toUpperCase().trim();
        return userRepository.findByReferralCode(normalized)
                .filter(User::isActive)
                .map(user -> ReferralCodeCheckResponseDto.of(normalized, true, user.getName()))
                .orElse(ReferralCodeCheckResponseDto.of(normalized, false, null));
    }

    private String generateUniqueReferralCode() {
        String referralCode;

        do {
            referralCode = generateReferralCode();
        } while (userRepository.existsByReferralCode(referralCode));

        return referralCode;
    }

    private String generateReferralCode() {
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < REFERRAL_CODE_LENGTH; i++) {
            int index = RANDOM.nextInt(REFERRAL_CODE_CHARS.length());
            code.append(REFERRAL_CODE_CHARS.charAt(index));
        }

        return code.toString();
    }

    private String maskLoginId(String loginId) {
        if (loginId == null || loginId.length() <= 2) return "***";
        return loginId.charAt(0) + "*".repeat(loginId.length() - 2) + loginId.charAt(loginId.length() - 1);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private List<Long> normalizeInterestCategoryIds(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return List.of();
        }

        if (categoryIds.stream().anyMatch(Objects::isNull)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        List<Long> normalizedIds = categoryIds.stream()
                .distinct()
                .toList();

        if (!categoryRepository.existsAllActiveByIds(normalizedIds)) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        return normalizedIds;
    }
}
