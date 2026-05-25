package com.sashimi.user.infrastructure.persistence;

import com.sashimi.user.domain.model.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, Long> {

    Optional<UserJpaEntity> findByLoginId(String loginId);

    Optional<UserJpaEntity> findByEmail(String email);

    Optional<UserJpaEntity> findByReferralCode(String referralCode);

    boolean existsByLoginId(String loginId);

    boolean existsByEmail(String email);

    void deleteByStatusAndDeactivatedAtBefore(UserStatus status, LocalDateTime dateTime);

    boolean existsByReferralCode(String referralCode);
}
