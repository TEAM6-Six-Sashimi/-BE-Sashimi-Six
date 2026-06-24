package com.sashimi.user.infrastructure.persistence;

import com.sashimi.user.domain.model.Role;
import com.sashimi.user.domain.model.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, Long> {

    Optional<UserJpaEntity> findByLoginId(String loginId);

    Optional<UserJpaEntity> findByEmail(String email);

    Optional<UserJpaEntity> findByReferralCode(String referralCode);

    boolean existsByLoginId(String loginId);

    boolean existsByEmail(String email);


    boolean existsByReferralCode(String referralCode);

    List<UserJpaEntity> findAllByStatusAndDeactivatedAtBefore(
            UserStatus status,
            LocalDateTime dateTime
    );

    @Query("""
            SELECT u FROM UserJpaEntity u
            WHERE u.status <> com.sashimi.user.domain.model.UserStatus.DELETED
            AND (:role IS NULL OR u.role = :role)
            AND (
                :keyword IS NULL OR :keyword = '' OR
                u.name LIKE CONCAT('%', :keyword, '%') OR
                u.loginId LIKE CONCAT('%', :keyword, '%') OR
                u.email LIKE CONCAT('%', :keyword, '%')
            )
            ORDER BY u.createdAt DESC
            """)
    List<UserJpaEntity> searchForAdmin(
            @Param("keyword") String keyword,
            @Param("role") Role role
    );
}
