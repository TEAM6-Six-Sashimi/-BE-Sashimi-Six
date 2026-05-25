package com.sashimi.user.application.result;

import com.sashimi.user.domain.model.UserStatus;

public record WithdrawUserResult(
        UserStatus status
) {
}