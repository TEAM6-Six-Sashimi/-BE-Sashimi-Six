package com.sashimi.subscription.application.service;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.subscription.application.usecase.SubscriptionCommandUseCase;
import com.sashimi.subscription.domain.model.Subscription;
import com.sashimi.subscription.domain.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SubscriptionCommandService
        implements SubscriptionCommandUseCase {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionCommandService(
            SubscriptionRepository subscriptionRepository
    ) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public CancelResult cancel(Long userId) {
        Subscription subscription =
                subscriptionRepository
                        .findActiveByUserIdForUpdate(userId)
                        .orElseThrow(() ->
                                new BusinessException(
                                        ErrorCode.SUBSCRIPTION_NOT_FOUND
                                )
                        );

        Subscription cancelled =
                subscription.cancelRenewal();

        Subscription saved =
                subscriptionRepository.save(cancelled);

        return new CancelResult(
                saved.getId(),
                saved.getPlan().name(),
                saved.getStatus().name(),
                saved.isAutoRenew(),
                saved.getExpiredAt()
        );
    }
}