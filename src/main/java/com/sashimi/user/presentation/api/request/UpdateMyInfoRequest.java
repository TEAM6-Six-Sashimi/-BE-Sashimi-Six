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

    private String phone;

    private boolean marketingConsent;
    private boolean emailConsent;
    private boolean aiConsent;

    public UpdateMyInfoCommand toCommand(Long userId) {
        return new UpdateMyInfoCommand(
                userId,
                currentPassword,
                phone,
                marketingConsent,
                emailConsent,
                aiConsent
        );
    }
}