package com.sashimi.user.domain.model;

import java.time.LocalDateTime;

public class User {

    private Long id;
    private String name;
    private String loginId;
    private String password;
    private String email;
    private Role role;
    private UserStatus status;
    private boolean emailVerified;
    private String referralCode;
    private LocalDateTime deactivatedAt;

    public User(Long id, String name, String loginId, String password, String email,
                Role role, UserStatus status, boolean emailVerified,
                String referralCode, LocalDateTime deactivatedAt) {
        this.id = id;
        this.name = name;
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.role = role;
        this.status = status;
        this.emailVerified = emailVerified;
        this.referralCode = referralCode;
        this.deactivatedAt = deactivatedAt;
    }

    public static User createStudent(String name, String loginId, String password,
                                     String email, String referralCode) {
        return new User(null, name, loginId, password, email,
                Role.STUDENT, UserStatus.ACTIVE, true, referralCode, null);
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
    public Role getRole() { return role; }
    public UserStatus getStatus() { return status; }
    public boolean isEmailVerified() { return emailVerified; }
    public String getReferralCode() { return referralCode; }
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
        this.emailVerified = false;
        this.referralCode = null;
    }
}

