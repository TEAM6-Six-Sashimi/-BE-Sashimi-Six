package com.sashimi.user.presentation.api;

import com.sashimi.user.application.usecase.AdminUserQueryUseCase;
import com.sashimi.user.presentation.api.response.AdminUserDetailResponse;
import com.sashimi.user.presentation.api.response.AdminUserListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Tag(name = "어드민 회원 관리 API", description = "전체 회원 목록 조회 및 상세 조회 API (ROLE_ADMIN 전용)")
@SecurityRequirement(name = "bearerAuth")
public class AdminUserController {

    private final AdminUserQueryUseCase adminUserQueryUseCase;

    @Operation(summary = "전체 회원 목록 조회", description = "이름/아이디/이메일 검색, 역할 필터, 가입일 최신순 정렬. ROLE_ADMIN 권한 필요.")
    @GetMapping
    public ResponseEntity<List<AdminUserListResponse>> getUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role
    ) {
        return ResponseEntity.ok(adminUserQueryUseCase.getUsers(keyword, role));
    }

    @Operation(summary = "회원 상세 조회", description = "특정 회원의 기본 정보를 조회합니다. ROLE_ADMIN 권한 필요.")
    @GetMapping("/{userId}")
    public ResponseEntity<AdminUserDetailResponse> getUserDetail(@PathVariable Long userId) {
        return ResponseEntity.ok(adminUserQueryUseCase.getUserDetail(userId));
    }
}
