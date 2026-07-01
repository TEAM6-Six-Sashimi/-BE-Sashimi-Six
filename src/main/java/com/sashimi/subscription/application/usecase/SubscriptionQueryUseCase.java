package com.sashimi.subscription.application.usecase;

import java.time.LocalDateTime;
import java.util.List;

public interface SubscriptionQueryUseCase {

    List<PlanResult> getPlans();

    PreviewResult getPreview(Long userId, String planCode);

    MySubscriptionResult getMySubscription(Long userId);

    PaymentHistoryResult getPaymentHistory(Long userId, int page, int size);

    record PlanResult(
            String planCode,
            String planName,
            int durationMonths,
            Long originalPrice,
            Long price,
            int discountRate,
            String planThumbnail,
            List<String> features
    ) {
    }

    record PreviewResult(
            PlanResult plan,
            Long creditBalance,
            Long balanceAfterPayment,
            Long insufficientAmount,
            boolean alreadySubscribed,
            boolean purchasable
    ) {
    }

    record MySubscriptionResult(
            boolean subscribed,
            Long subscriptionId,
            String planCode,
            String planName,
            String status,
            LocalDateTime startedAt,
            LocalDateTime expiresAt,
            LocalDateTime nextBillingAt,
            boolean autoRenew,
            boolean cancellable
    ) {
    }

    record PaymentHistoryResult(
            List<PaymentHistoryItem> items,
            long totalElements,
            int totalPages,
            int page,
            int size
    ) {
    }

    record PaymentHistoryItem(
            Long subscriptionPaymentId,
            Long subscriptionId,
            Long orderId,
            String orderNo,
            String planCode,
            String planName,
            Long amount,
            String billingType,
            LocalDateTime paidAt
    ) {
    }
}