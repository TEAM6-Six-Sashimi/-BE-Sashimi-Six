package com.sashimi.user.presentation.api.response;

import com.sashimi.user.domain.model.UserStatus;

public record WithdrawUserResult(
        UserStatus status
) {
}