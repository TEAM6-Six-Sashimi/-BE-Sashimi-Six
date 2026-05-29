package com.sashimi.cart.application.event;

import com.sashimi.cart.domain.repository.CartItemRepository;
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
public class UserWithdrawalCartCleanupHandler {

    private final CartItemRepository cartItemRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(UserWithdrawnEvent event) {
        cartItemRepository.deleteAllByUserId(event.userId());
        log.info("Cart items cleaned up after user withdrawal. userId={}", event.userId());
    }
}
