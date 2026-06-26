package com.sashimi.payment.application.service.checkout;

import com.sashimi.credit.application.command.UseCreditCommand;
import com.sashimi.credit.application.result.CreditBalanceResult;
import com.sashimi.credit.application.usecase.CreditCommandUseCase;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.payment.application.command.PaymentCheckoutCommand;
import com.sashimi.payment.application.command.PaymentPurchaseType;
import com.sashimi.payment.application.usecase.PaymentCommandUseCase.PaidSubscription;
import com.sashimi.payment.application.usecase.PaymentCommandUseCase.PaymentResult;
import com.sashimi.subscription.domain.model.Subscription;
import com.sashimi.subscription.domain.model.SubscriptionPayment;
import com.sashimi.subscription.domain.model.SubscriptionPlan;
import com.sashimi.subscription.domain.repository.SubscriptionPaymentRepository;
import com.sashimi.subscription.domain.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionCheckoutProcessor implements PaymentCheckoutProcessor {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPaymentRepository subscriptionPaymentRepository;
    private final CreditCommandUseCase creditCommandUseCase;
    private final OrderPaymentWriter orderPaymentWriter;

    @Override
    public PaymentPurchaseType supports() {
        return PaymentPurchaseType.AI_SUBSCRIPTION;
    }

    @Override
    public PaymentResult checkout(
            PaymentCheckoutCommand command
    ) {
        validate(command);

        Long userId = command.userId();
        SubscriptionPlan plan = command.planCode();
        LocalDateTime now = LocalDateTime.now();

        subscriptionRepository.findActiveByUserIdForUpdate(userId)
                .ifPresent(subscription -> {
                    if (subscription.isActive(now)) {
                        throw new BusinessException(
                                ErrorCode.SUBSCRIPTION_ALREADY_ACTIVE
                        );
                    }

                    subscriptionRepository.save(
                            subscription.expire()
                    );
                });

        Subscription subscription =
                subscriptionRepository.save(
                        Subscription.start(
                                userId,
                                plan,
                                now
                        )
                );

        OrderPaymentWriter.SubscriptionOrderPayment saved =
                orderPaymentWriter.saveSubscriptionPayment(
                        userId,
                        subscription,
                        plan
                );

        CreditBalanceResult credit =
                creditCommandUseCase.useCredit(
                        new UseCreditCommand(
                                userId,
                                saved.payment().getAmount()
                        )
                );

        subscriptionPaymentRepository.save(
                SubscriptionPayment.initial(
                        subscription.getId(),
                        saved.order().getId(),
                        saved.payment().getId(),
                        userId,
                        saved.order().getOrderNo(),
                        plan,
                        saved.payment().getPaidAt()
                )
        );

        log.info(
                "AI 구독권 결제 완료 - userId={}, subscriptionId={}, orderId={}, paymentId={}, plan={}, amount={}",
                userId,
                subscription.getId(),
                saved.order().getId(),
                saved.payment().getId(),
                plan.name(),
                saved.payment().getAmount()
        );

        return new PaymentResult(
                saved.order().getId(),
                saved.order().getOrderNo(),
                saved.payment().getId(),
                PaymentPurchaseType.AI_SUBSCRIPTION,
                saved.payment().getAmount(),
                saved.payment().getStatus().name(),
                credit.balance(),
                List.of(),
                new PaidSubscription(
                        subscription.getId(),
                        plan.name(),
                        plan.getPlanName(),
                        subscription.getStatus().name(),
                        subscription.getStartedAt(),
                        subscription.getExpiredAt(),
                        subscription.getNextBillingAt()
                )
        );
    }

    private void validate(
            PaymentCheckoutCommand command
    ) {
        if (command.courseId() != null) {
            throw new BusinessException(
                    ErrorCode.PAYMENT_SUBSCRIPTION_COURSE_ID_NOT_ALLOWED
            );
        }

        if (command.planCode() == null) {
            throw new BusinessException(
                    ErrorCode.PAYMENT_SUBSCRIPTION_PLAN_REQUIRED
            );
        }
    }
}