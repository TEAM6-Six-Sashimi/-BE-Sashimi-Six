package com.sashimi.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FindLoginIdConfirmRequestDto {

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "인증 코드는 필수입니다.")
    @Pattern(
            regexp = "^[A-Za-z0-9]{8}$",
            message = "인증 코드는 영문과 숫자로 구성된 8자리여야 합니다."
    )
    private String code;
}
