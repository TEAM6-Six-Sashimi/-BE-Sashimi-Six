package com.sashimi.auth.service;

import com.sashimi.auth.application.policy.SignupEligibility;
import com.sashimi.auth.application.policy.SignupEligibilityPolicy;
import com.sashimi.security.blacklist.TokenBlacklistService;
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
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
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

    private static final String REFERRAL_CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int REFERRAL_CODE_LENGTH = 8;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final AuthenticationManager authenticationManager;
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

        eventPublisher.publishEvent(
                new UserPasswordChangedEvent(
                        savedUser.getId(),
                        savedUser.getEmail(),
                        LocalDateTime.now()
                )
        );

        return new PasswordResetConfirmResponseDto(true, true);
    }

    public TokenResponseDto login(LoginRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLoginId(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByLoginId(authentication.getName())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        TokenResponseDto tokenResponse = jwtTokenProvider.generateToken(authentication);

        LocalDateTime refreshExpiryDate = LocalDateTime.now()
                .plusNanos(jwtTokenProvider.getRefreshTokenValidityInMilliseconds() * 1_000_000);

        refreshService.saveOrUpdate(user, tokenResponse.getRefreshToken(), refreshExpiryDate);

        return tokenResponse.withName(user.getName());
    }

    public TokenResponseDto reissue(String refreshTokenValue) {
        RefreshToken refreshToken = refreshService.findValidRefreshToken(refreshTokenValue);
        refreshToken.markUsed(LocalDateTime.now());

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getLoginId(),
                "",
                com.sashimi.security.principal.CustomUserPrincipal.from(user).getAuthorities()
        );

        TokenResponseDto tokenResponse = jwtTokenProvider.generateToken(authentication);

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
