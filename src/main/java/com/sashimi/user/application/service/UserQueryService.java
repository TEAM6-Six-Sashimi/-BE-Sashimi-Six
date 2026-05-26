package com.sashimi.user.application.service;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.user.application.usecase.UserQueryUseCase;
import com.sashimi.user.domain.model.User;
import com.sashimi.user.domain.repository.UserRepository;
import com.sashimi.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService implements UserQueryUseCase {

    private final UserRepository userRepository;

    @Override
    public UserResponseDto getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!user.isActive()) {
            throw new BusinessException(ErrorCode.INACTIVE_USER);
        }

        return UserResponseDto.from(user);
    }
}