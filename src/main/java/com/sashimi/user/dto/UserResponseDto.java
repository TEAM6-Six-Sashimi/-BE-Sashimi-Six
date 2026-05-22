package com.sashimi.user.dto;

import com.sashimi.user.entity.User;
import com.sashimi.user.model.Role;
import com.sashimi.user.model.UserStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDto {

    private Long id;
    private String name;
    private String loginId;
    private String email;
    private Role role;
    private UserStatus status;
    private boolean emailVerified;

    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .loginId(user.getLoginId())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .emailVerified(user.isEmailVerified())
                .build();
    }
}