package com.sashimi.user.application.usecase;

import com.sashimi.user.application.command.ChangePasswordCommand;
import com.sashimi.user.application.command.UpdateMyInfoCommand;
import com.sashimi.user.application.command.WithdrawUserCommand;
import com.sashimi.user.application.result.WithdrawUserResult;
import com.sashimi.user.dto.UserResponseDto;
import com.sashimi.user.application.result.ChangePasswordResult;

public interface UserCommandUseCase {

    UserResponseDto updateMyInfo(UpdateMyInfoCommand command);

    ChangePasswordResult changePassword(ChangePasswordCommand command);

    WithdrawUserResult withdraw(WithdrawUserCommand command);


}