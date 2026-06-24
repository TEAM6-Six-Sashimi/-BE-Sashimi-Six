package com.sashimi.user.application.usecase;

import com.sashimi.user.presentation.api.response.AdminUserDetailResponse;
import com.sashimi.user.presentation.api.response.AdminUserListResponse;

import java.util.List;

public interface AdminUserQueryUseCase {

    List<AdminUserListResponse> getUsers(String keyword, String role);

    AdminUserDetailResponse getUserDetail(Long userId);
}
