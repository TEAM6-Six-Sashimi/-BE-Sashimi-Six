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
import com.sashimi.subscription.domain.model.Subscription;
import com.sashimi.subscription.domain.model.SubscriptionPayment;
import com.sashimi.subscription.domain.repository.SubscriptionPaymentRepository;
import com.sashimi.subscription.domain.repository.SubscriptionRepository;
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

    public SubscriptionRenewalProcessor(
            SubscriptionRepository subscriptionRepository,
            SubscriptionPaymentRepository subscriptionPaymentRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            PaymentRepository paymentRepository,
            CreditRepository creditRepository
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionPaymentRepository =
                subscriptionPaymentRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.creditRepository = creditRepository;
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

        Credit credit =
                creditRepository.findByUserIdForUpdate(
                        subscription.getUserId()
                ).orElse(null);

        if (credit == null
                || credit.getBalance() < subscription.getPrice()) {
            subscriptionRepository.save(
                    subscription.expire()
            );

            return RenewalResult.EXPIRED_INSUFFICIENT_CREDIT;
        }

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

        subscriptionRepository.save(
                subscription.renew(
                        subscription.getNextBillingAt()
                )
        );

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
        EXPIRED_INSUFFICIENT_CREDIT,
        SKIPPED
    }
}