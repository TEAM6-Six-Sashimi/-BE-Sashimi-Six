package com.sashimi.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReferralCodeCheckResponseDto {

    private String referralCode;
    private boolean available;
    private String referrerName;

    public static ReferralCodeCheckResponseDto of(String referralCode, boolean available, String referrerName) {
        return ReferralCodeCheckResponseDto.builder()
                .referralCode(referralCode)
                .available(available)
                .referrerName(referrerName)
                .build();
    }
}
