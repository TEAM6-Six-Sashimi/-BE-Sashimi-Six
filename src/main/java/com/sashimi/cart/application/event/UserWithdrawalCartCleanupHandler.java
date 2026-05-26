package com.sashimi.cart.application.event;

import com.sashimi.cart.domain.repository.CartItemRepository;
import com.sashimi.user.application.event.UserWithdrawnEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserWithdrawalCartCleanupHandler {

    private final CartItemRepository cartItemRepository;

    @EventListener
    public void handle(UserWithdrawnEvent event) {
        cartItemRepository.deleteAllByUserId(event.userId());
    }
}
