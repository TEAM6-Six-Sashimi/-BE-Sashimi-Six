package com.sashimi.user.application.usecase;

import com.sashimi.user.application.command.ChangePasswordCommand;
import com.sashimi.user.application.command.UpdateMyInfoCommand;
import com.sashimi.user.application.command.WithdrawUserCommand;
import com.sashimi.user.dto.UserResponseDto;

public interface UserCommandUseCase {

    UserResponseDto updateMyInfo(UpdateMyInfoCommand command);

    void changePassword(ChangePasswordCommand command);

    void withdraw(WithdrawUserCommand command);
}