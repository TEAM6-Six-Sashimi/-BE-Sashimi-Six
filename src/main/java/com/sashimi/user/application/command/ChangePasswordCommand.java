package com.sashimi.user.application.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChangePasswordCommand {

    private final Long userId;
    private final String currentPassword;
    private final String newPassword;
}
