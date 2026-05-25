package com.sashimi.user.presentation.api.request;

import com.sashimi.user.application.command.WithdrawUserCommand;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WithdrawUserRequest {

    @NotBlank(message = "현재 비밀번호는 필수입니다.")
    private String currentPassword;

    public WithdrawUserCommand toCommand(Long userId) {
        return new WithdrawUserCommand(
                userId,
                currentPassword
        );
    }
}
