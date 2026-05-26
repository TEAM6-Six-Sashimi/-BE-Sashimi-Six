package com.sashimi.user.application.result;

public record ChangePasswordResult(
        boolean passwordChanged,
        boolean requiresLogin
) {
}
