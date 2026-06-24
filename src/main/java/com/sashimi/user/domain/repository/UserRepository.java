package com.sashimi.user.domain.repository;

import com.sashimi.user.domain.model.User;
import com.sashimi.user.domain.model.UserStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(Long id);

    Optional<User> findByLoginId(String loginId);

    Optional<User> findByEmail(String email);

    boolean existsByLoginId(String loginId);

    boolean existsByEmail(String email);

    User save(User user);


    Optional<User> findByReferralCode(String referralCode);
    boolean existsByReferralCode(String referralCode);

    List<User> findAllByStatusAndDeactivatedAtBefore(UserStatus status, LocalDateTime dateTime);

    List<User> findAllForAdmin();
}