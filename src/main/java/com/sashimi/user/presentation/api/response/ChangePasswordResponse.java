package com.sashimi.user.presentation.api.response;

public record ChangePasswordResponse(
        boolean passwordChanged,
        boolean requiresLogin,
        String accessToken,
        String refreshToken
) {
}