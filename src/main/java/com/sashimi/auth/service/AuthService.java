package com.sashimi.auth.service;

import com.sashimi.auth.dto.LoginRequestDto;
import com.sashimi.auth.dto.PasswordResetRequestDto;
import com.sashimi.auth.dto.TokenResponseDto;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.security.jwt.JwtTokenProvider;
import com.sashimi.token.entity.RefreshToken;
import com.sashimi.token.service.RefreshService;
import com.sashimi.user.domain.model.User;
import com.sashimi.user.domain.repository.UserRepository;
import com.sashimi.user.dto.LoginIdCheckResponseDto;
import com.sashimi.user.dto.SignupRequestDto;
import com.sashimi.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sashimi.verification.application.usecase.EmailVerificationUseCase;
import com.sashimi.verification.domain.model.VerificationPurpose;
import com.sashimi.auth.dto.PasswordResetConfirmRequestDto;
import com.sashimi.verification.application.command.ConfirmEmailVerificationCommand;
import com.sashimi.verification.application.command.RequestEmailVerificationCommand;


import java.util.Locale;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RefreshService refreshService;
    private final EmailVerificationUseCase emailVerificationUseCase;

    public UserResponseDto register(SignupRequestDto request) {
        if (userRepository.existsByLoginId(request.getLoginId())) {
            throw new BusinessException(ErrorCode.DUPLICATE_LOGIN_ID);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        emailVerificationUseCase.validateVerifiedEmail(
                request.getEmail(),
                VerificationPurpose.SIGNUP
        );

        User user = User.createStudent(
                request.getName(),
                request.getLoginId(),
                passwordEncoder.encode(request.getPassword()),
                request.getEmail(),
                request.getReferralCode()
        );

        User savedUser = userRepository.save(user);

        return UserResponseDto.from(savedUser);
    }

    public void requestPasswordReset(PasswordResetRequestDto request) {
        String email = normalizeEmail(request.getEmail());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!user.isActive()) {
            throw new BusinessException(ErrorCode.INACTIVE_USER);
        }

        emailVerificationUseCase.requestEmailVerification(
                new RequestEmailVerificationCommand(
                        email,
                        VerificationPurpose.PASSWORD_RESET,
                        user.getId()
                )
        );
    }

    public void resetPassword(PasswordResetConfirmRequestDto request) {
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
        userRepository.save(user);

        refreshService.deleteByUser(user);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
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

        return tokenResponse;
    }

    public TokenResponseDto reissue(String refreshTokenValue) {
        RefreshToken refreshToken = refreshService.findValidRefreshToken(refreshTokenValue);

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

        return tokenResponse;
    }

    public void logout(String refreshTokenValue) {
        RefreshToken refreshToken = refreshService.findValidRefreshToken(refreshTokenValue);

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        refreshService.deleteByUser(user);
    }

    @Transactional(readOnly = true)
    public LoginIdCheckResponseDto checkLoginId(String loginId) {
        return LoginIdCheckResponseDto.of(loginId, !userRepository.existsByLoginId(loginId));
    }
}