package com.sashimi.user.application.service;

import com.sashimi.user.application.event.UserWithdrawnEvent;
import com.sashimi.user.domain.model.User;
import com.sashimi.user.domain.model.UserStatus;
import com.sashimi.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserWithdrawalFinalizationService {

    private static final Period WITHDRAWAL_GRACE_PERIOD = Period.ofYears(1);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void finalizeExpiredWithdrawals() {
        LocalDateTime threshold = LocalDateTime.now().minus(WITHDRAWAL_GRACE_PERIOD);

        List<User> users = userRepository.findAllByStatusAndDeactivatedAtBefore(
                UserStatus.INACTIVE,
                threshold
        );

        for (User user : users) {
            String maskedLoginId = "d" + user.getId();
            String maskedEmail = "deleted_" + user.getId() + "@deleted.local";
            String encodedPassword = passwordEncoder.encode(UUID.randomUUID().toString());

            user.withdraw(maskedLoginId, maskedEmail, encodedPassword);
            User savedUser = userRepository.save(user);

            eventPublisher.publishEvent(
                    new UserWithdrawnEvent(savedUser.getId(), savedUser.getDeactivatedAt())
            );
        }
    }
}