package com.sashimi.user.application.usecase;

import com.sashimi.user.dto.UserResponseDto;

public interface UserQueryUseCase {

    UserResponseDto getMyInfo(Long userId);
}