package com.sashimi.subscription.domain.repository;

import com.sashimi.subscription.domain.model.SubscriptionPayment;

import java.util.List;

public interface SubscriptionPaymentRepository {

    SubscriptionPayment save(
            SubscriptionPayment subscriptionPayment
    );

    PageResult findAllByUserId(
            Long userId,
            int page,
            int size
    );

    record PageResult(
            List<SubscriptionPayment> content,
            long totalElements,
            int totalPages,
            int page,
            int size
    ) {
    }
}