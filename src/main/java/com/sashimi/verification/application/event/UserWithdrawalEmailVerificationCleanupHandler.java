package com.sashimi.verification.application.event;

import com.sashimi.user.application.event.UserWithdrawnEvent;
import com.sashimi.verification.domain.repository.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserWithdrawalEmailVerificationCleanupHandler {

    private final EmailVerificationRepository emailVerificationRepository;

    @EventListener
    public void handle(UserWithdrawnEvent event) {
        emailVerificationRepository.deleteAllByUserId(event.userId());
    }
}