package com.sashimi.user.application.service;

import com.sashimi.auth.dto.TokenResponseDto;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.security.jwt.JwtTokenProvider;
import com.sashimi.security.principal.CustomUserPrincipal;
import com.sashimi.token.service.RefreshService;
import com.sashimi.user.application.command.ChangePasswordCommand;
import com.sashimi.user.application.command.UpdateMyInfoCommand;
import com.sashimi.user.application.command.WithdrawUserCommand;
import com.sashimi.user.application.event.UserPasswordChangedEvent;
import com.sashimi.user.presentation.api.response.WithdrawUserResult;
import com.sashimi.user.application.usecase.UserCommandUseCase;
import com.sashimi.user.domain.model.User;
import com.sashimi.user.domain.repository.UserRepository;
import com.sashimi.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sashimi.user.presentation.api.response.ChangePasswordResult;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Transactional
public class UserAccountService implements UserCommandUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshService refreshService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ApplicationEventPublisher eventPublisher;


    @Override
    public UserResponseDto updateMyInfo(UpdateMyInfoCommand command) {
        User user = getActiveUser(command.getUserId());
        verifyCurrentPassword(user, command.getCurrentPassword());

        user.updateProfile(command.getPhone(),
                command.isMarketingConsent(), command.isEmailConsent(), command.isAiConsent());
        User savedUser = userRepository.save(user);

        return UserResponseDto.from(savedUser);
    }

    @Override
    public ChangePasswordResult changePassword(ChangePasswordCommand command) {
        User user = getActiveUser(command.getUserId());
        verifyCurrentPassword(user, command.getCurrentPassword());

        if (passwordEncoder.matches(command.getNewPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.SAME_AS_OLD_PASSWORD);
        }

        user.changePassword(passwordEncoder.encode(command.getNewPassword()));
        User savedUser = userRepository.save(user);

        refreshService.deleteByUser(savedUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                savedUser.getLoginId(),
                "",
                CustomUserPrincipal.from(savedUser).getAuthorities()
        );
        TokenResponseDto tokenResponse = jwtTokenProvider.generateToken(authentication);
        LocalDateTime refreshExpiryDate = LocalDateTime.now()
                .plusNanos(jwtTokenProvider.getRefreshTokenValidityInMilliseconds() * 1_000_000);
        refreshService.saveOrUpdate(savedUser, tokenResponse.getRefreshToken(), refreshExpiryDate);

        eventPublisher.publishEvent(
                new UserPasswordChangedEvent(
                        savedUser.getId(),
                        savedUser.getEmail(),
                        LocalDateTime.now()
                )
        );

        return new ChangePasswordResult(true, false, tokenResponse.getAccessToken(), tokenResponse.getRefreshToken());
    }

    private User getActiveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!user.isActive()) {
            throw new BusinessException(ErrorCode.INACTIVE_USER);
        }

        return user;
    }

    private void verifyCurrentPassword(User user, String currentPassword) {
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CURRENT_PASSWORD);
        }
    }

    @Override
    public WithdrawUserResult withdraw(WithdrawUserCommand command) {
        User user = getActiveUser(command.getUserId());
        verifyCurrentPassword(user, command.getCurrentPassword());

        user.deactivate();
        User savedUser = userRepository.save(user);

        refreshService.deleteByUser(savedUser);

        return new WithdrawUserResult(savedUser.getStatus());
    }
}
