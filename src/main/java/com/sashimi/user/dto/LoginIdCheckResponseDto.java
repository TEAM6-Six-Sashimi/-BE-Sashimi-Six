package com.sashimi.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginIdCheckResponseDto {

    private String loginId;
    private boolean available;

    public static LoginIdCheckResponseDto of(String loginId, boolean available) {
        return LoginIdCheckResponseDto.builder()
                .loginId(loginId)
                .available(available)
                .build();
    }
}