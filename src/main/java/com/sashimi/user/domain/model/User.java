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
    private String phone;
    private boolean marketingConsent;
    private boolean emailConsent;
    private boolean aiConsent;

    public User(Long id, String name, String loginId, String password, String email, String phone, LocalDate birthDate,
                Role role, UserStatus status, boolean emailVerified,
                String referralCode, List<Long> interestCategoryIds, LocalDateTime deactivatedAt,
                boolean marketingConsent, boolean emailConsent, boolean aiConsent) {
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
        this.phone = phone;
        this.marketingConsent = marketingConsent;
        this.emailConsent = emailConsent;
        this.aiConsent = aiConsent;
    }

    public String getPhone() {
        return phone;
    }

    public static User createStudent(String name, String loginId, String password,
                                     String email, String phone, LocalDate birthDate,
                                     String referralCode, List<Long> interestCategoryIds,
                                     boolean marketingConsent, boolean emailConsent, boolean aiConsent) {
        return new User(null, name, loginId, password, email, phone, birthDate,
                Role.STUDENT, UserStatus.ACTIVE, true, referralCode, interestCategoryIds, null,
                marketingConsent, emailConsent, aiConsent);
    }

    public void updateProfile(String name, String email, boolean marketingConsent, boolean emailConsent, boolean aiConsent) {
        this.name = name;
        this.email = email;
        this.marketingConsent = marketingConsent;
        this.emailConsent = emailConsent;
        this.aiConsent = aiConsent;
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
    public boolean isMarketingConsent() { return marketingConsent; }
    public boolean isEmailConsent() { return emailConsent; }
    public boolean isAiConsent() { return aiConsent; }

    public void promoteToInstructor() {
        this.role = Role.INSTRUCTOR;
    }

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

