package com.sashimi.user.dto;

import com.sashimi.user.domain.model.Role;
import com.sashimi.user.domain.model.User;
import com.sashimi.user.domain.model.UserStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class UserResponseDto {

    private Long id;
    private String name;
    private String loginId;
    private String email;
    private LocalDate birthDate;
    private Role role;
    private UserStatus status;
    private boolean emailVerified;
    private String referralCode;
    private List<Long> interestCategoryIds;

    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .loginId(user.getLoginId())
                .email(user.getEmail())
                .birthDate(user.getBirthDate())
                .role(user.getRole())
                .status(user.getStatus())
                .emailVerified(user.isEmailVerified())
                .referralCode(user.getReferralCode())
                .interestCategoryIds(user.getInterestCategoryIds())
                .build();
    }
}
