package com.sashimi.user.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequestDto {

    @NotBlank
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9]{6,20}$",
            message = "아이디는 영문과 숫자를 포함한 6~20자여야 합니다."
    )
    private String loginId;

    @NotBlank
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9\\p{Punct}]{8,16}$",
            message = "비밀번호는 영문과 숫자를 포함한 8~16자여야 합니다."
    )
    @NotBlank
    private String password;


    @NotBlank
    private String passwordConfirm;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String name;

    // 추천인 코드는 나중에 확장 가능하게 일단 선택값으로 둬도 됨
    private String referralCode;

    @AssertTrue(message = "비밀번호와 비밀번호 확인이 일치하지 않습니다.")
    public boolean isPasswordMatched() {
        return password != null && password.equals(passwordConfirm);
    }
}
