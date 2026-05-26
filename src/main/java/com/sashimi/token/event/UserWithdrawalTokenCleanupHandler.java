package com.sashimi.token.event;

import com.sashimi.token.repository.RefreshTokenRepository;
import com.sashimi.user.application.event.UserWithdrawnEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserWithdrawalTokenCleanupHandler {

    private final RefreshTokenRepository refreshTokenRepository;

    @EventListener
    public void handle(UserWithdrawnEvent event) {
        refreshTokenRepository.deleteByUserId(event.userId());
    }
}
