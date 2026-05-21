package com.sashimi.user.repository;

import com.sashimi.user.entity.User;
import com.sashimi.user.model.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    void deleteByStatusAndDeactivatedAtBefore(UserStatus status, LocalDateTime dateTime);
}