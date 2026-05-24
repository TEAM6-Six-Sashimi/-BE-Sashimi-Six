package com.sashimi.user.presentation.api.request;

import com.sashimi.user.application.command.UpdateMyInfoCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateMyInfoRequest {

    @NotBlank(message = "현재 비밀번호는 필수입니다.")
    private String currentPassword;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    public UpdateMyInfoCommand toCommand(Long userId) {
        return new UpdateMyInfoCommand(
                userId,
                currentPassword,
                name,
                email
        );
    }
}