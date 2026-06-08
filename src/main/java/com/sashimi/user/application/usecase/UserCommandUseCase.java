package com.sashimi.user.application.usecase;

import com.sashimi.user.application.command.ChangePasswordCommand;
import com.sashimi.user.application.command.UpdateMyInfoCommand;
import com.sashimi.user.application.command.WithdrawUserCommand;
import com.sashimi.user.presentation.api.response.WithdrawUserResult;
import com.sashimi.user.dto.UserResponseDto;
import com.sashimi.user.presentation.api.response.ChangePasswordResult;

public interface UserCommandUseCase {

    UserResponseDto updateMyInfo(UpdateMyInfoCommand command);

    ChangePasswordResult changePassword(ChangePasswordCommand command);

    WithdrawUserResult withdraw(WithdrawUserCommand command);


}