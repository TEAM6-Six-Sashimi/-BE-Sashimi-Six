package com.sashimi.verification.application.event;

import com.sashimi.user.application.event.UserWithdrawnEvent;
import com.sashimi.verification.domain.repository.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserWithdrawalEmailVerificationCleanupHandler {

    private final EmailVerificationRepository emailVerificationRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(UserWithdrawnEvent event) {
        emailVerificationRepository.deleteAllByUserId(event.userId());
        log.info("Email verification records cleaned up after user withdrawal. userId={}", event.userId());
    }
}