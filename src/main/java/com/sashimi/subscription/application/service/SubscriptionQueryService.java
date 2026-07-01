package com.sashimi.subscription.application.service;

import com.sashimi.credit.application.usecase.CreditQueryUseCase;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.subscription.application.usecase.SubscriptionQueryUseCase;
import com.sashimi.subscription.domain.model.SubscriptionPlan;
import com.sashimi.subscription.domain.repository.SubscriptionPaymentRepository;
import com.sashimi.subscription.domain.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class SubscriptionQueryService
        implements SubscriptionQueryUseCase {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPaymentRepository subscriptionPaymentRepository;
    private final CreditQueryUseCase creditQueryUseCase;

    public SubscriptionQueryService(
            SubscriptionRepository subscriptionRepository,
            SubscriptionPaymentRepository subscriptionPaymentRepository,
            CreditQueryUseCase creditQueryUseCase
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionPaymentRepository =
                subscriptionPaymentRepository;
        this.creditQueryUseCase = creditQueryUseCase;
    }

    @Override
    public List<PlanResult> getPlans() {
        return Arrays.stream(SubscriptionPlan.values())
                .map(this::toPlanResult)
                .toList();
    }

    @Override
    public PreviewResult getPreview(
            Long userId,
            String planCode
    ) {
        SubscriptionPlan plan = parsePlan(planCode);

        long creditBalance =
                creditQueryUseCase.getBalance(userId).balance();

        boolean alreadySubscribed =
                subscriptionRepository.findActiveByUserId(
                        userId,
                        LocalDateTime.now()
                ).isPresent();

        long balanceAfterPayment =
                Math.max(creditBalance - plan.getPrice(), 0L);

        long insufficientAmount =
                Math.max(plan.getPrice() - creditBalance, 0L);

        boolean purchasable =
                !alreadySubscribed
                        && insufficientAmount == 0L;

        return new PreviewResult(
                toPlanResult(plan),
                creditBalance,
                balanceAfterPayment,
                insufficientAmount,
                alreadySubscribed,
                purchasable
        );
    }

    @Override
    public MySubscriptionResult getMySubscription(
            Long userId
    ) {
        return subscriptionRepository.findActiveByUserId(
                        userId,
                        LocalDateTime.now()
                )
                .map(subscription ->
                        new MySubscriptionResult(
                                true,
                                subscription.getId(),
                                subscription.getPlan().name(),
                                subscription.getPlan().getPlanName(),
                                subscription.getStatus().name(),
                                subscription.getStartedAt(),
                                subscription.getExpiredAt(),
                                subscription.getNextBillingAt(),
                                subscription.isAutoRenew(),
                                subscription.isAutoRenew()
                        )
                )
                .orElseGet(() ->
                        new MySubscriptionResult(
                                false,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                false,
                                false
                        )
                );
    }

    @Override
    public PaymentHistoryResult getPaymentHistory(
            Long userId,
            int page,
            int size
    ) {
        if (page < 0 || size < 1 || size > 100) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT_VALUE
            );
        }

        SubscriptionPaymentRepository.PageResult result =
                subscriptionPaymentRepository.findAllByUserId(
                        userId,
                        page,
                        size
                );

        List<PaymentHistoryItem> items =
                result.content()
                        .stream()
                        .map(payment ->
                                new PaymentHistoryItem(
                                        payment.getId(),
                                        payment.getSubscriptionId(),
                                        payment.getOrderId(),
                                        payment.getOrderNo(),
                                        payment.getPlan().name(),
                                        payment.getPlan().getPlanName(),
                                        payment.getAmount(),
                                        payment.getBillingType().name(),
                                        payment.getPaidAt()
                                )
                        )
                        .toList();

        return new PaymentHistoryResult(
                items,
                result.totalElements(),
                result.totalPages(),
                result.page(),
                result.size()
        );
    }

    private SubscriptionPlan parsePlan(String planCode) {
        if (planCode == null || planCode.isBlank()) {
            throw new BusinessException(
                    ErrorCode.SUBSCRIPTION_INVALID_PLAN
            );
        }

        try {
            return SubscriptionPlan.valueOf(
                    planCode.toUpperCase(Locale.ROOT)
            );
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(
                    ErrorCode.SUBSCRIPTION_INVALID_PLAN
            );
        }
    }

    private PlanResult toPlanResult(
            SubscriptionPlan plan
    ) {
        return new PlanResult(
                plan.name(),
                plan.getPlanName(),
                plan.getDurationMonths(),
                plan.getOriginalPrice(),
                plan.getPrice(),
                plan.getDiscountRate(),
                plan.getPlanThumbnail(),
                plan.getFeatures()
        );
    }
}