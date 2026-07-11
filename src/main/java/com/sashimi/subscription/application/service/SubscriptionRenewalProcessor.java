package com.sashimi.subscription.application.service;

import com.sashimi.credit.domain.model.Credit;
import com.sashimi.credit.domain.repository.CreditRepository;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.order.domain.model.Order;
import com.sashimi.order.domain.model.OrderItem;
import com.sashimi.order.domain.repository.OrderItemRepository;
import com.sashimi.order.domain.repository.OrderRepository;
import com.sashimi.payment.domain.model.Payment;
import com.sashimi.payment.domain.repository.PaymentRepository;
import com.sashimi.subscription.application.event.SubscriptionRenewedEvent;
import com.sashimi.subscription.domain.model.Subscription;
import com.sashimi.subscription.domain.model.SubscriptionPayment;
import com.sashimi.subscription.domain.repository.SubscriptionPaymentRepository;
import com.sashimi.subscription.domain.repository.SubscriptionRepository;
import com.sashimi.user.domain.model.User;
import com.sashimi.user.domain.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class SubscriptionRenewalProcessor {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPaymentRepository subscriptionPaymentRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final CreditRepository creditRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository userRepository;

    public SubscriptionRenewalProcessor(
            SubscriptionRepository subscriptionRepository,
            SubscriptionPaymentRepository subscriptionPaymentRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            PaymentRepository paymentRepository,
            CreditRepository creditRepository,
            ApplicationEventPublisher eventPublisher,
            UserRepository userRepository
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionPaymentRepository =
                subscriptionPaymentRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.creditRepository = creditRepository;
        this.eventPublisher = eventPublisher;
        this.userRepository = userRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public RenewalResult renew(Long subscriptionId, LocalDateTime now) {
        Subscription subscription =
                subscriptionRepository
                        .findByIdForUpdate(subscriptionId)
                        .orElseThrow(() ->
                                new BusinessException(
                                        ErrorCode.SUBSCRIPTION_NOT_FOUND
                                )
                        );

        if (!subscription.isRenewalDue(now)) {
            return RenewalResult.SKIPPED;
        }

        if (subscription.isGracePeriodExpired(now)) {
            subscriptionRepository.save(
                    subscription.expire()
            );

            return RenewalResult.EXPIRED_GRACE_PERIOD_ENDED;
        }

        Credit credit =
                creditRepository.findByUserIdForUpdate(
                        subscription.getUserId()
                ).orElse(null);

        if (credit == null
                || credit.getBalance() < subscription.getPrice()) {
            subscriptionRepository.save(
                    subscription.markPastDue(now)
            );

            return RenewalResult.PAST_DUE_INSUFFICIENT_CREDIT;
        }

        LocalDateTime renewalBaseAt = subscription.getNextBillingAt();

        credit.use(subscription.getPrice());
        creditRepository.save(credit);

        Order order = orderRepository.save(
                Order.paid(createOrderNo(), subscription.getPrice(), subscription.getUserId()));

        orderItemRepository.save(
                OrderItem.createSubscription(
                        subscription.getPlan().getPlanName(),
                        subscription.getPrice(),
                        order.getId(),
                        subscription.getId()
                )
        );

        Payment payment = paymentRepository.save(
                Payment.paid(subscription.getPrice(), order.getId(), subscription.getUserId()));

        subscriptionPaymentRepository.save(
                SubscriptionPayment.renewal(
                        subscription.getId(),
                        order.getId(),
                        payment.getId(),
                        subscription.getUserId(),
                        order.getOrderNo(),
                        subscription.getPlan(),
                        payment.getPaidAt()
                )
        );

        Subscription renewed = subscriptionRepository.save(
                subscription.renew(renewalBaseAt)
        );

        User user = userRepository.findById(subscription.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        eventPublisher.publishEvent(new SubscriptionRenewedEvent(
                subscription.getUserId(),
                user.getEmail(),
                user.getName(),
                subscription.getPlan().getPlanName(),
                renewed.getNextBillingAt()
        ));

        return RenewalResult.RENEWED;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean expireCancelled(Long subscriptionId, LocalDateTime now) {
        Subscription subscription =
                subscriptionRepository
                        .findByIdForUpdate(subscriptionId)
                        .orElseThrow(() ->
                                new BusinessException(
                                        ErrorCode.SUBSCRIPTION_NOT_FOUND
                                )
                        );

        if (!subscription.isExpirationDue(now)) {
            return false;
        }

        subscriptionRepository.save(subscription.expire());

        return true;
    }

    private String createOrderNo() {
        return "ORD-"
                + LocalDateTime.now().format(
                DateTimeFormatter.ofPattern(
                        "yyyyMMddHHmmssSSS"
                )
        )
                + "-"
                + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }

    public enum RenewalResult {
        RENEWED,
        PAST_DUE_INSUFFICIENT_CREDIT,
        EXPIRED_GRACE_PERIOD_ENDED,
        SKIPPED
    }
}