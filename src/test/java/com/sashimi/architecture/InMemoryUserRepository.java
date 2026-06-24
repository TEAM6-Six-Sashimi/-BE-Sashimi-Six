package com.sashimi.architecture;

import com.sashimi.user.domain.model.User;
import com.sashimi.user.domain.model.UserStatus;
import com.sashimi.user.domain.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> store = new HashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            User saved = new User(
                    sequence.getAndIncrement(),
                    user.getName(), user.getLoginId(), user.getPassword(),
                    user.getEmail(), user.getPhone(), user.getBirthDate(),
                    user.getRole(), user.getStatus(), user.isEmailVerified(),
                    user.getReferralCode(), user.getInterestCategoryIds(),
                    user.getCreatedAt(), user.getDeactivatedAt(), user.getLastLoginAt(),
                    user.isMarketingConsent(), user.isEmailConsent(), user.isAiConsent()
            );
            store.put(saved.getId(), saved);
            return saved;
        }
        store.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<User> findByLoginId(String loginId) {
        return store.values().stream()
                .filter(u -> u.getLoginId().equals(loginId))
                .findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return store.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public Optional<User> findByReferralCode(String referralCode) {
        return store.values().stream()
                .filter(u -> referralCode.equals(u.getReferralCode()))
                .findFirst();
    }

    @Override
    public boolean existsByLoginId(String loginId) {
        return store.values().stream().anyMatch(u -> u.getLoginId().equals(loginId));
    }

    @Override
    public boolean existsByEmail(String email) {
        return store.values().stream().anyMatch(u -> u.getEmail().equals(email));
    }

    @Override
    public boolean existsByReferralCode(String referralCode) {
        return store.values().stream().anyMatch(u -> referralCode.equals(u.getReferralCode()));
    }

    @Override
    public List<User> findAllByStatusAndDeactivatedAtBefore(UserStatus status, LocalDateTime dateTime) {
        return store.values().stream()
                .filter(u -> u.getStatus() == status)
                .filter(u -> u.getDeactivatedAt() != null && u.getDeactivatedAt().isBefore(dateTime))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findAllForAdmin() {
        return store.values().stream()
                .filter(u -> u.getStatus() != UserStatus.DELETED)
                .sorted(Comparator.comparing(User::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }
}
