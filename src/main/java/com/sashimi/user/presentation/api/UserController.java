package com.sashimi.user.presentation.api;

import com.sashimi.security.principal.CustomUserPrincipal;
import com.sashimi.user.application.result.WithdrawUserResult;
import com.sashimi.user.application.usecase.UserCommandUseCase;
import com.sashimi.user.application.usecase.UserQueryUseCase;
import com.sashimi.user.dto.UserResponseDto;
import com.sashimi.user.presentation.api.request.ChangePasswordRequest;
import com.sashimi.user.presentation.api.request.UpdateMyInfoRequest;
import com.sashimi.user.presentation.api.request.WithdrawUserRequest;
import com.sashimi.user.presentation.api.response.WithdrawUserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.sashimi.user.application.result.ChangePasswordResult;
import com.sashimi.user.presentation.api.response.ChangePasswordResponse;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserCommandUseCase userCommandUseCase;
    private final UserQueryUseCase userQueryUseCase;

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
                result.requiresLogin()
        ));
    }

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

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMyInfo(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ResponseEntity.ok(userQueryUseCase.getMyInfo(principal.getId()));
    }
}
