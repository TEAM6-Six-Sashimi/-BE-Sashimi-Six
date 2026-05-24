package com.sashimi.auth.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PasswordResetConfirmRequestDto {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "인증 코드는 필수입니다.")
    @Pattern(
            regexp = "^[A-Za-z0-9]{8}$",
            message = "인증 코드는 영문과 숫자로 구성된 8자리여야 합니다."
    )
    private String code;

    @NotBlank(message = "새 비밀번호는 필수입니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9\\p{Punct}]{8,16}$",
            message = "비밀번호는 영문과 숫자를 포함한 8~16자여야 합니다."
    )
    private String newPassword;

    @NotBlank(message = "새 비밀번호 확인은 필수입니다.")
    private String newPasswordConfirm;

    @AssertTrue(message = "새 비밀번호와 새 비밀번호 확인이 일치하지 않습니다.")
    public boolean isNewPasswordMatched() {
        return newPassword != null && newPassword.equals(newPasswordConfirm);
    }
}
