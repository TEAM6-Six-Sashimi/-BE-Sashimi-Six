package com.sashimi.auth.service;

import com.sashimi.auth.dto.LoginRequestDto;
import com.sashimi.auth.dto.TokenResponseDto;
import com.sashimi.security.jwt.JwtTokenProvider;
import com.sashimi.token.entity.RefreshToken;
import com.sashimi.token.service.RefreshService;
import com.sashimi.user.dto.LoginIdCheckResponseDto;
import com.sashimi.user.dto.SignupRequestDto;
import com.sashimi.user.dto.UserResponseDto;
import com.sashimi.user.entity.User;
import com.sashimi.user.model.Role;
import com.sashimi.user.model.UserStatus;
import com.sashimi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public UserResponseDto register(SignupRequestDto request) {
        if (userRepository.existsByLoginId(request.getLoginId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        User user = User.builder()
                .loginId(request.getLoginId())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .name(request.getName())
                .role(Role.STUDENT)
                .status(UserStatus.ACTIVE)
                .emailVerified(true) // 7단계 이메일 인증 붙이면 검증 결과로 바꿀 예정
                .referralCode(request.getReferralCode())
                .build();

        return UserResponseDto.from(userRepository.save(user));
    }

    public TokenResponseDto login(LoginRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLoginId(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByLoginId(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        TokenResponseDto tokenResponse = jwtTokenProvider.generateToken(authentication);

        LocalDateTime refreshExpiryDate = LocalDateTime.now()
                .plusNanos(jwtTokenProvider.getRefreshTokenValidityInMilliseconds() * 1_000_000);

        refreshService.saveOrUpdate(user, tokenResponse.getRefreshToken(), refreshExpiryDate);

        return tokenResponse;
    }

    public TokenResponseDto reissue(String refreshTokenValue) {
        RefreshToken refreshToken = refreshService.findValidRefreshToken(refreshTokenValue);
        User user = refreshToken.getUser();

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
        refreshService.deleteByUser(refreshToken.getUser());
    }

    @Transactional(readOnly = true)
    public LoginIdCheckResponseDto checkLoginId(String loginId) {
        return LoginIdCheckResponseDto.of(loginId, !userRepository.existsByLoginId(loginId));
    }
}