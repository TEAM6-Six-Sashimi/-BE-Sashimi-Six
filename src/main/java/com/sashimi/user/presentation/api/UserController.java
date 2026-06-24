package com.sashimi.user.presentation.api;

import com.sashimi.security.principal.CustomUserPrincipal;
import com.sashimi.user.presentation.api.response.WithdrawUserResult;
import com.sashimi.user.application.usecase.UserCommandUseCase;
import com.sashimi.user.application.usecase.UserQueryUseCase;
import com.sashimi.user.dto.UserResponseDto;
import com.sashimi.user.presentation.api.request.ChangePasswordRequest;
import com.sashimi.user.presentation.api.request.UpdateMyInfoRequest;
import com.sashimi.user.presentation.api.request.WithdrawUserRequest;
import com.sashimi.user.presentation.api.response.WithdrawUserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.sashimi.user.presentation.api.response.ChangePasswordResult;
import com.sashimi.user.presentation.api.response.ChangePasswordResponse;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "회원 API", description = "내 정보 조회, 수정, 비밀번호 변경, 회원 탈퇴 API")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserCommandUseCase userCommandUseCase;
    private final UserQueryUseCase userQueryUseCase;

    @Operation(summary = "내 정보 수정", description = "JWT 인증된 사용자의 이름과 이메일을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내 정보 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 현재 비밀번호 불일치"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "비활성 회원"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "중복된 이메일"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PatchMapping("/me")
    public ResponseEntity<UserResponseDto> updateMyInfo(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody @Valid UpdateMyInfoRequest request
    ) {
        UserResponseDto response = userCommandUseCase.updateMyInfo(
                request.toCommand(principal.getId())
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "비밀번호 변경", description = "JWT 인증된 사용자의 비밀번호를 변경하고 기존 refresh token을 무효화합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
            @ApiResponse(responseCode = "400", description = "현재 비밀번호 불일치 또는 기존 비밀번호와 동일"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "비활성 회원"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PatchMapping("/me/password")
    public ResponseEntity<ChangePasswordResponse> changePassword(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody @Valid ChangePasswordRequest request
    ) {
        ChangePasswordResult result = userCommandUseCase.changePassword(
                request.toCommand(principal.getId())
        );

        return ResponseEntity.ok(new ChangePasswordResponse(
                result.passwordChanged(),
                result.requiresLogin(),
                result.accessToken(),
                result.refreshToken()
        ));
    }

    @Operation(summary = "회원 탈퇴", description = "JWT 인증된 사용자를 비활성 상태로 변경하고 refresh token을 삭제합니다. 유예 기간 이후 최종 탈퇴 처리에서 개인정보를 익명화합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 요청 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 현재 비밀번호 불일치"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "비활성 회원"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/me")
    public ResponseEntity<WithdrawUserResponse> withdraw(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody @Valid WithdrawUserRequest request
    ) {
        WithdrawUserResult result = userCommandUseCase.withdraw(
                request.toCommand(principal.getId())
        );

        return ResponseEntity.ok(new WithdrawUserResponse(result.status()));
    }

    @Operation(summary = "내 정보 조회", description = "JWT 인증된 사용자의 회원 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내 정보 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "비활성 회원"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMyInfo(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ResponseEntity.ok(userQueryUseCase.getMyInfo(principal.getId()));
    }
}
