package com.sashimi.token.event;

import com.sashimi.token.repository.RefreshTokenRepository;
import com.sashimi.user.application.event.UserWithdrawnEvent;
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
public class UserWithdrawalTokenCleanupHandler {

    private final RefreshTokenRepository refreshTokenRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(UserWithdrawnEvent event) {
        refreshTokenRepository.deleteByUserId(event.userId());
        log.info("Refresh token cleaned up after user withdrawal. userId={}", event.userId());
    }
}