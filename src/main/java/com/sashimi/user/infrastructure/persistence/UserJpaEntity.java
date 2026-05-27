package com.sashimi.user.infrastructure.persistence;

import com.sashimi.user.domain.model.Role;
import com.sashimi.user.domain.model.User;
import com.sashimi.user.domain.model.UserStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_interest_categories",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "category_id", nullable = false)
    private Set<Long> interestCategoryIds = new LinkedHashSet<>();

    @Column(name = "deleted_at")
    private LocalDateTime deactivatedAt;

    public static UserJpaEntity from(User user) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.id = user.getId();
        entity.name = user.getName();
        entity.loginId = user.getLoginId();
        entity.password = user.getPassword();
        entity.email = user.getEmail();
        entity.birthDate = user.getBirthDate();
        entity.role = user.getRole();
        entity.status = user.getStatus();
        entity.emailVerified = user.isEmailVerified();
        entity.referralCode = user.getReferralCode();
        entity.interestCategoryIds = new LinkedHashSet<>(user.getInterestCategoryIds());
        entity.deactivatedAt = user.getDeactivatedAt();
        return entity;
    }

    public User toDomain() {
        return new User(id, name, loginId, password, email, birthDate, role, status,
                emailVerified, referralCode, List.copyOf(interestCategoryIds), deactivatedAt);
    }
}
