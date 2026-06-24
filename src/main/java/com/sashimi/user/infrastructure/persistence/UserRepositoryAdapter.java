package com.sashimi.user.infrastructure.persistence;

import com.sashimi.user.domain.model.Role;
import com.sashimi.user.domain.model.User;
import com.sashimi.user.domain.model.UserStatus;
import com.sashimi.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository springDataUserRepository;

    @Override
    public Optional<User> findById(Long id) {
        return springDataUserRepository.findById(id).map(UserJpaEntity::toDomain);
    }

    @Override
    public Optional<User> findByLoginId(String loginId) {
        return springDataUserRepository.findByLoginId(loginId).map(UserJpaEntity::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return springDataUserRepository.findByEmail(email).map(UserJpaEntity::toDomain);
    }

    @Override
    public boolean existsByLoginId(String loginId) {
        return springDataUserRepository.existsByLoginId(loginId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return springDataUserRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByReferralCode(String referralCode) {
        return springDataUserRepository.existsByReferralCode(referralCode);
    }

    @Override
    public User save(User user) {
        return springDataUserRepository.save(UserJpaEntity.from(user)).toDomain();
    }


    @Override
    public Optional<User> findByReferralCode(String referralCode) {
        return springDataUserRepository.findByReferralCode(referralCode)
                .map(UserJpaEntity::toDomain);
    }

    @Override
    public List<User> findAllByStatusAndDeactivatedAtBefore(
            UserStatus status,
            LocalDateTime dateTime
    ) {
        return springDataUserRepository
                .findAllByStatusAndDeactivatedAtBefore(status, dateTime)
                .stream()
                .map(UserJpaEntity::toDomain)
                .toList();
    }

    @Override
    public List<User> searchForAdmin(String keyword, Role role) {
        return springDataUserRepository.searchForAdmin(keyword, role)
                .stream()
                .map(UserJpaEntity::toDomain)
                .toList();
    }
}
