package com.sashimi.user.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class User {

    private Long id;
    private String name;
    private String loginId;
    private String password;
    private String email;
    private LocalDate birthDate;
    private Role role;
    private UserStatus status;
    private boolean emailVerified;
    private String referralCode;
    private List<Long> interestCategoryIds;
    private LocalDateTime deactivatedAt;

    public User(Long id, String name, String loginId, String password, String email, LocalDate birthDate,
                Role role, UserStatus status, boolean emailVerified,
                String referralCode, List<Long> interestCategoryIds, LocalDateTime deactivatedAt) {
        this.id = id;
        this.name = name;
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.birthDate = birthDate;
        this.role = role;
        this.status = status;
        this.emailVerified = emailVerified;
        this.referralCode = referralCode;
        this.interestCategoryIds = interestCategoryIds == null ? List.of() : List.copyOf(interestCategoryIds);
        this.deactivatedAt = deactivatedAt;
    }

    public static User createStudent(String name, String loginId, String password,
                                     String email, LocalDate birthDate, String referralCode,
                                     List<Long> interestCategoryIds) {
        return new User(null, name, loginId, password, email, birthDate,
                Role.STUDENT, UserStatus.ACTIVE, true, referralCode, interestCategoryIds, null);
    }

    public void updateProfile(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
        this.deactivatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getLoginId() { return loginId; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public LocalDate getBirthDate() { return birthDate; }
    public Role getRole() { return role; }
    public UserStatus getStatus() { return status; }
    public boolean isEmailVerified() { return emailVerified; }
    public String getReferralCode() { return referralCode; }
    public List<Long> getInterestCategoryIds() { return interestCategoryIds; }
    public LocalDateTime getDeactivatedAt() { return deactivatedAt; }

    public void withdraw(String maskedLoginId, String maskedEmail, String encodedPassword) {
        this.status = UserStatus.DELETED;
        if (this.deactivatedAt == null) {
            this.deactivatedAt = LocalDateTime.now();
        }
        this.name = "탈퇴회원";
        this.loginId = maskedLoginId;
        this.email = maskedEmail;
        this.password = encodedPassword;
        this.birthDate = null;
        this.emailVerified = false;
        this.referralCode = null;
        this.interestCategoryIds = List.of();
    }
}

