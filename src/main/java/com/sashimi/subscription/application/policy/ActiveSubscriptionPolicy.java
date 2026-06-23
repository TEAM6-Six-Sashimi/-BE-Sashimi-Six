package com.sashimi.subscription.application.policy;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.subscription.domain.repository.SubscriptionRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ActiveSubscriptionPolicy {

    private final SubscriptionRepository subscriptionRepository;

    public ActiveSubscriptionPolicy(
            SubscriptionRepository subscriptionRepository
    ) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public void validate(Long userId) {
        if (subscriptionRepository.findActiveByUserId(
                userId,
                LocalDateTime.now()
        ).isEmpty()) {
            throw new BusinessException(
                    ErrorCode.SUBSCRIPTION_REQUIRED
            );
        }
    }
}