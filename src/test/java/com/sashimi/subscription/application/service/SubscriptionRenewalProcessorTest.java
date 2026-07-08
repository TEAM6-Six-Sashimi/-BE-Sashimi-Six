package com.sashimi.subscription.application.service;

import com.sashimi.credit.domain.model.Credit;
import com.sashimi.credit.domain.repository.CreditRepository;
import com.sashimi.order.domain.model.Order;
import com.sashimi.order.domain.model.OrderItem;
import com.sashimi.order.domain.model.OrderStatus;
import com.sashimi.order.domain.repository.OrderItemRepository;
import com.sashimi.order.domain.repository.OrderRepository;
import com.sashimi.payment.domain.model.Payment;
import com.sashimi.payment.domain.model.PaymentStatus;
import com.sashimi.payment.domain.repository.PaymentRepository;
import com.sashimi.subscription.domain.model.Subscription;
import com.sashimi.subscription.domain.model.SubscriptionBillingType;
import com.sashimi.subscription.domain.model.SubscriptionPayment;
import com.sashimi.subscription.domain.model.SubscriptionPlan;
import com.sashimi.subscription.domain.model.SubscriptionStatus;
import com.sashimi.subscription.domain.repository.SubscriptionPaymentRepository;
import com.sashimi.subscription.domain.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.sashimi.subscription.application.service.SubscriptionRenewalProcessor.RenewalResult.EXPIRED_INSUFFICIENT_CREDIT;
import static com.sashimi.subscription.application.service.SubscriptionRenewalProcessor.RenewalResult.RENEWED;
import static com.sashimi.subscription.application.service.SubscriptionRenewalProcessor.RenewalResult.SKIPPED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SubscriptionRenewalProcessorTest {

    private static final Long USER_ID = 1L;
    private static final Long SUBSCRIPTION_ID = 10L;
    private static final Long ORDER_ID = 100L;
    private static final Long PAYMENT_ID = 200L;

    private SubscriptionRepository subscriptionRepository;
    private SubscriptionPaymentRepository subscriptionPaymentRepository;
    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;
    private PaymentRepository paymentRepository;
    private CreditRepository creditRepository;

    private SubscriptionRenewalProcessor renewalProcessor;

    @BeforeEach
    void setUp() {
        subscriptionRepository = mock(SubscriptionRepository.class);
        subscriptionPaymentRepository =
                mock(SubscriptionPaymentRepository.class);
        orderRepository = mock(OrderRepository.class);
        orderItemRepository = mock(OrderItemRepository.class);
        paymentRepository = mock(PaymentRepository.class);
        creditRepository = mock(CreditRepository.class);

        renewalProcessor = new SubscriptionRenewalProcessor(
                subscriptionRepository,
                subscriptionPaymentRepository,
                orderRepository,
                orderItemRepository,
                paymentRepository,
                creditRepository
        );

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);

                    return Order.restore(
                            ORDER_ID,
                            order.getOrderNo(),
                            order.getTotalAmount(),
                            order.getDiscountAmount(),
                            order.getFinalAmount(),
                            OrderStatus.PAID,
                            order.getCreatedAt(),
                            order.getUserId()
                    );
                });

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> {
                    Payment payment = invocation.getArgument(0);

                    return Payment.restore(
                            PAYMENT_ID,
                            payment.getAmount(),
                            PaymentStatus.PAID,
                            payment.getPaidAt(),
                            payment.getCreatedAt(),
                            payment.getOrderId(),
                            payment.getUserId()
                    );
                });

        when(creditRepository.save(any(Credit.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(subscriptionRepository.save(any(Subscription.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(orderItemRepository.save(any(OrderItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(subscriptionPaymentRepository.save(
                any(SubscriptionPayment.class)
        )).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void 크레딧이_충분하면_구독을_자동_갱신() {
        LocalDateTime now = LocalDateTime.of(
                2026,
                7,
                7,
                10,
                0
        );
        LocalDateTime nextBillingAt = now.minusMinutes(1);

        Subscription subscription = activeAutoRenewSubscription(
                nextBillingAt
        );
        Credit credit = new Credit(
                1L,
                USER_ID,
                30_000L
        );

        when(subscriptionRepository.findByIdForUpdate(
                SUBSCRIPTION_ID
        )).thenReturn(Optional.of(subscription));

        when(creditRepository.findByUserIdForUpdate(
                USER_ID
        )).thenReturn(Optional.of(credit));

        SubscriptionRenewalProcessor.RenewalResult result =
                renewalProcessor.renew(
                        SUBSCRIPTION_ID,
                        now
                );

        assertThat(result).isEqualTo(RENEWED);

        ArgumentCaptor<Credit> creditCaptor =
                ArgumentCaptor.forClass(Credit.class);

        verify(creditRepository).save(creditCaptor.capture());

        assertThat(creditCaptor.getValue().getBalance())
                .isEqualTo(20_000L);

        ArgumentCaptor<Subscription> subscriptionCaptor =
                ArgumentCaptor.forClass(Subscription.class);

        verify(subscriptionRepository)
                .save(subscriptionCaptor.capture());

        Subscription renewedSubscription =
                subscriptionCaptor.getValue();

        assertThat(renewedSubscription.getStatus())
                .isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(renewedSubscription.isAutoRenew())
                .isTrue();
        assertThat(renewedSubscription.getExpiredAt())
                .isEqualTo(nextBillingAt.plusMonths(1));
        assertThat(renewedSubscription.getNextBillingAt())
                .isEqualTo(nextBillingAt.plusMonths(1));

        ArgumentCaptor<SubscriptionPayment> paymentCaptor =
                ArgumentCaptor.forClass(SubscriptionPayment.class);

        verify(subscriptionPaymentRepository)
                .save(paymentCaptor.capture());

        SubscriptionPayment subscriptionPayment =
                paymentCaptor.getValue();

        assertThat(subscriptionPayment.getSubscriptionId())
                .isEqualTo(SUBSCRIPTION_ID);
        assertThat(subscriptionPayment.getUserId())
                .isEqualTo(USER_ID);
        assertThat(subscriptionPayment.getBillingType())
                .isEqualTo(SubscriptionBillingType.RENEWAL);
        assertThat(subscriptionPayment.getAmount())
                .isEqualTo(SubscriptionPlan.MONTHLY.getPrice());

        verify(orderRepository).save(any(Order.class));
        verify(orderItemRepository).save(any(OrderItem.class));
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void 크레딧이_부족하면_구독을_만료_처리() {
        LocalDateTime now = LocalDateTime.of(
                2026,
                7,
                7,
                10,
                0
        );
        LocalDateTime nextBillingAt = now.minusMinutes(1);

        Subscription subscription = activeAutoRenewSubscription(
                nextBillingAt
        );
        Credit credit = new Credit(
                1L,
                USER_ID,
                5_000L
        );

        when(subscriptionRepository.findByIdForUpdate(
                SUBSCRIPTION_ID
        )).thenReturn(Optional.of(subscription));

        when(creditRepository.findByUserIdForUpdate(
                USER_ID
        )).thenReturn(Optional.of(credit));

        SubscriptionRenewalProcessor.RenewalResult result =
                renewalProcessor.renew(
                        SUBSCRIPTION_ID,
                        now
                );

        assertThat(result).isEqualTo(EXPIRED_INSUFFICIENT_CREDIT);

        ArgumentCaptor<Subscription> subscriptionCaptor =
                ArgumentCaptor.forClass(Subscription.class);

        verify(subscriptionRepository)
                .save(subscriptionCaptor.capture());

        Subscription expiredSubscription =
                subscriptionCaptor.getValue();

        assertThat(expiredSubscription.getStatus())
                .isEqualTo(SubscriptionStatus.EXPIRED);
        assertThat(expiredSubscription.isAutoRenew())
                .isFalse();
        assertThat(expiredSubscription.getNextBillingAt())
                .isNull();

        verify(creditRepository, never())
                .save(any(Credit.class));
        verify(orderRepository, never())
                .save(any(Order.class));
        verify(orderItemRepository, never())
                .save(any(OrderItem.class));
        verify(paymentRepository, never())
                .save(any(Payment.class));
        verify(subscriptionPaymentRepository, never())
                .save(any(SubscriptionPayment.class));
    }

    @Test
    void 다음_결제일이_미래이면_자동_갱신X() {
        LocalDateTime now = LocalDateTime.of(
                2026,
                7,
                7,
                10,
                0
        );
        LocalDateTime futureBillingAt = now.plusDays(1);

        Subscription subscription = activeAutoRenewSubscription(
                futureBillingAt
        );

        when(subscriptionRepository.findByIdForUpdate(
                SUBSCRIPTION_ID
        )).thenReturn(Optional.of(subscription));

        SubscriptionRenewalProcessor.RenewalResult result =
                renewalProcessor.renew(
                        SUBSCRIPTION_ID,
                        now
                );

        assertThat(result).isEqualTo(SKIPPED);

        verify(creditRepository, never())
                .findByUserIdForUpdate(USER_ID);
        verify(subscriptionRepository, never())
                .save(any(Subscription.class));
        verify(orderRepository, never())
                .save(any(Order.class));
        verify(paymentRepository, never())
                .save(any(Payment.class));
        verify(subscriptionPaymentRepository, never())
                .save(any(SubscriptionPayment.class));
    }

    @Test
    void 해지된_구독은_만료일이_지나면_만료_처리() {
        LocalDateTime now = LocalDateTime.of(
                2026,
                7,
                7,
                10,
                0
        );
        LocalDateTime expiredAt = now.minusMinutes(1);

        Subscription subscription = cancelledSubscription(
                expiredAt
        );

        when(subscriptionRepository.findByIdForUpdate(
                SUBSCRIPTION_ID
        )).thenReturn(Optional.of(subscription));

        boolean result = renewalProcessor.expireCancelled(
                SUBSCRIPTION_ID,
                now
        );

        assertThat(result).isTrue();

        ArgumentCaptor<Subscription> subscriptionCaptor =
                ArgumentCaptor.forClass(Subscription.class);

        verify(subscriptionRepository)
                .save(subscriptionCaptor.capture());

        Subscription expiredSubscription =
                subscriptionCaptor.getValue();

        assertThat(expiredSubscription.getStatus())
                .isEqualTo(SubscriptionStatus.EXPIRED);
        assertThat(expiredSubscription.isAutoRenew())
                .isFalse();
        assertThat(expiredSubscription.getNextBillingAt())
                .isNull();

        verify(creditRepository, never())
                .findByUserIdForUpdate(USER_ID);
        verify(orderRepository, never())
                .save(any(Order.class));
        verify(paymentRepository, never())
                .save(any(Payment.class));
        verify(subscriptionPaymentRepository, never())
                .save(any(SubscriptionPayment.class));
    }

    private Subscription activeAutoRenewSubscription(
            LocalDateTime nextBillingAt
    ) {
        return Subscription.restore(
                SUBSCRIPTION_ID,
                SubscriptionPlan.MONTHLY,
                SubscriptionStatus.ACTIVE,
                SubscriptionPlan.MONTHLY.getPrice(),
                nextBillingAt.minusMonths(1),
                nextBillingAt,
                nextBillingAt,
                true,
                USER_ID
        );
    }

    private Subscription cancelledSubscription(
            LocalDateTime expiredAt
    ) {
        return Subscription.restore(
                SUBSCRIPTION_ID,
                SubscriptionPlan.MONTHLY,
                SubscriptionStatus.ACTIVE,
                SubscriptionPlan.MONTHLY.getPrice(),
                expiredAt.minusMonths(1),
                expiredAt,
                null,
                false,
                USER_ID
        );
    }
}