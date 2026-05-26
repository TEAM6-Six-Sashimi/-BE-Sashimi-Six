package com.sashimi.user.presentation.api.response;

public record ChangePasswordResult(
        boolean passwordChanged,
        boolean requiresLogin
) {
}
