package com.sashimi.user.application.service;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.token.service.RefreshService;
import com.sashimi.user.application.command.ChangePasswordCommand;
import com.sashimi.user.application.command.UpdateMyInfoCommand;
import com.sashimi.user.application.command.WithdrawUserCommand;
import com.sashimi.user.presentation.api.response.WithdrawUserResult;
import com.sashimi.user.application.usecase.UserCommandUseCase;
import com.sashimi.user.domain.model.User;
import com.sashimi.user.domain.repository.UserRepository;
import com.sashimi.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sashimi.user.presentation.api.response.ChangePasswordResult;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAccountService implements UserCommandUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshService refreshService;

    @Override
    public UserResponseDto updateMyInfo(UpdateMyInfoCommand command) {
        User user = getActiveUser(command.getUserId());
        verifyCurrentPassword(user, command.getCurrentPassword());

        if (!user.getEmail().equals(command.getEmail())
                && userRepository.existsByEmail(command.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        user.updateProfile(command.getName(), command.getEmail());
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
        userRepository.save(user);

        refreshService.deleteByUser(user);

        return new ChangePasswordResult(true, true);
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
