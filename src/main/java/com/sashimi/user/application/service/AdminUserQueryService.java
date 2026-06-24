package com.sashimi.user.application.service;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.user.application.usecase.AdminUserQueryUseCase;
import com.sashimi.user.domain.model.Role;
import com.sashimi.user.domain.model.User;
import com.sashimi.user.domain.repository.UserRepository;
import com.sashimi.user.presentation.api.response.AdminUserDetailResponse;
import com.sashimi.user.presentation.api.response.AdminUserListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserQueryService implements AdminUserQueryUseCase {

    private final UserRepository userRepository;

    @Override
    public List<AdminUserListResponse> getUsers(String keyword, String role) {
        Role roleFilter = parseRole(role);
        String keywordFilter = (keyword == null || keyword.isBlank()) ? null : keyword.trim();

        return userRepository.searchForAdmin(keywordFilter, roleFilter)
                .stream()
                .map(AdminUserListResponse::from)
                .toList();
    }

    @Override
    public AdminUserDetailResponse getUserDetail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return AdminUserDetailResponse.from(user);
    }

    private Role parseRole(String role) {
        if (role == null || role.isBlank()) return null;
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }
}
