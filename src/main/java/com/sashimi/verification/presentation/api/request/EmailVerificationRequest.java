package com.sashimi.verification.presentation.api.request;

import com.sashimi.verification.application.command.RequestEmailVerificationCommand;
import com.sashimi.verification.domain.model.VerificationPurpose;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmailVerificationRequest {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String targetEmail;

    @NotNull(message = "인증 목적은 필수입니다.")
    private VerificationPurpose purpose;

    public RequestEmailVerificationCommand toCommand(Long userId) {
        return new RequestEmailVerificationCommand(
                targetEmail,
                purpose,
                userId
        );
    }
}
