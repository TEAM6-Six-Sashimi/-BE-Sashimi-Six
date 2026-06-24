package com.sashimi.user.infrastructure.persistence;

import com.sashimi.user.domain.model.Role;
import com.sashimi.user.domain.model.User;
import com.sashimi.user.domain.model.UserStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "login_id", nullable = false, length = 20)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    @Column(name = "referral_code", length = 50)
    private String referralCode;

    @Convert(converter = LongListStringConverter.class)
    @Column(name = "interest_category_ids", length = 255)
    private List<Long> interestCategoryIds = List.of();

    @Column(name = "phone", length = 20)
    private String phone;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deactivatedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "marketing_consent", nullable = false)
    private boolean marketingConsent;

    @Column(name = "email_consent", nullable = false)
    private boolean emailConsent;

    @Column(name = "ai_consent", nullable = false)
    private boolean aiConsent;

    public static UserJpaEntity from(User user) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.id = user.getId();
        entity.name = user.getName();
        entity.loginId = user.getLoginId();
        entity.password = user.getPassword();
        entity.email = user.getEmail();
        entity.phone = user.getPhone();
        entity.birthDate = user.getBirthDate();
        entity.role = user.getRole();
        entity.status = user.getStatus();
        entity.emailVerified = user.isEmailVerified();
        entity.referralCode = user.getReferralCode();
        entity.interestCategoryIds = user.getInterestCategoryIds();
        entity.deactivatedAt = user.getDeactivatedAt();
        entity.lastLoginAt = user.getLastLoginAt();
        entity.marketingConsent = user.isMarketingConsent();
        entity.emailConsent = user.isEmailConsent();
        entity.aiConsent = user.isAiConsent();
        return entity;
    }

    public User toDomain() {
        return new User(id, name, loginId, password, email, phone, birthDate, role, status,
                emailVerified, referralCode, interestCategoryIds, createdAt, deactivatedAt,
                lastLoginAt, marketingConsent, emailConsent, aiConsent);
    }
}
