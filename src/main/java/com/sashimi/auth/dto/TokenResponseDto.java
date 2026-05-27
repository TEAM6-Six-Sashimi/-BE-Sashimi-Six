package com.sashimi.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponseDto {

    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpiresIn;
    private String name;

    public TokenResponseDto withName(String name) {
        return TokenResponseDto.builder()
                .grantType(this.grantType)
                .accessToken(this.accessToken)
                .refreshToken(this.refreshToken)
                .accessTokenExpiresIn(this.accessTokenExpiresIn)
                .name(name)
                .build();
    }
}