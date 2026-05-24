package com.sashimi.user.presentation.api;

import com.sashimi.security.principal.CustomUserPrincipal;
import com.sashimi.user.application.usecase.UserCommandUseCase;
import com.sashimi.user.dto.UserResponseDto;
import com.sashimi.user.presentation.api.request.ChangePasswordRequest;
import com.sashimi.user.presentation.api.request.UpdateMyInfoRequest;
import com.sashimi.user.presentation.api.request.WithdrawUserRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserCommandUseCase userCommandUseCase;

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
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody @Valid ChangePasswordRequest request
    ) {
        userCommandUseCase.changePassword(
                request.toCommand(principal.getId())
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> withdraw(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody @Valid WithdrawUserRequest request
    ) {
        userCommandUseCase.withdraw(
                request.toCommand(principal.getId())
        );

        return ResponseEntity.noContent().build();
    }
}
