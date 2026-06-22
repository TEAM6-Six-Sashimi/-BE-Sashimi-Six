package com.sashimi.payment.application.service;

import com.sashimi.cart.application.port.CourseInfo;
import com.sashimi.cart.domain.model.CartItem;
import com.sashimi.cart.domain.repository.CartItemRepository;
import com.sashimi.credit.application.command.UseCreditCommand;
import com.sashimi.credit.application.result.CreditBalanceResult;
import com.sashimi.credit.application.usecase.CreditCommandUseCase;
import com.sashimi.enrollment.application.port.EnrollmentPort;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.order.application.policy.CoursePurchasePolicy;
import com.sashimi.order.domain.model.Order;
import com.sashimi.order.domain.model.OrderItem;
import com.sashimi.order.domain.model.OrderItemType;
import com.sashimi.order.domain.model.OrderStatus;
import com.sashimi.order.domain.repository.OrderItemRepository;
import com.sashimi.order.domain.repository.OrderRepository;
import com.sashimi.payment.application.command.PaymentCheckoutCommand;
import com.sashimi.payment.application.command.PaymentPurchaseType;
import com.sashimi.payment.domain.model.Payment;
import com.sashimi.payment.domain.model.PaymentStatus;
import com.sashimi.payment.domain.repository.PaymentRepository;
import com.sashimi.subscription.domain.model.Subscription;
import com.sashimi.subscription.domain.model.SubscriptionPayment;
import com.sashimi.subscription.domain.model.SubscriptionPlan;
import com.sashimi.subscription.domain.model.SubscriptionStatus;
import com.sashimi.subscription.domain.repository.SubscriptionPaymentRepository;
import com.sashimi.subscription.domain.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PaymentCommandServiceTest {

    private static final Long USER_ID = 1L;

    private static final Long COURSE_ID = 100L;
    private static final Long COURSE_PRICE = 30_000L;

    private static final Long ORDER_ID = 1L;
    private static final Long ORDER_ITEM_ID = 11L;
    private static final Long PAYMENT_ID = 20L;

    private static final Long SUBSCRIPTION_ID = 30L;
    private static final Long SUBSCRIPTION_ORDER_ITEM_ID = 31L;
    private static final Long MONTHLY_PRICE = 10_000L;

    private CartItemRepository cartItemRepository;
    private CoursePurchasePolicy coursePurchasePolicy;
    private EnrollmentPort enrollmentPort;
    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;
    private PaymentRepository paymentRepository;
    private CreditCommandUseCase creditCommandUseCase;
    private SubscriptionRepository subscriptionRepository;
    private SubscriptionPaymentRepository subscriptionPaymentRepository;

    private PaymentCommandService paymentCommandService;

    @BeforeEach
    void setUp() {
        cartItemRepository =
                mock(CartItemRepository.class);

        coursePurchasePolicy =
                mock(CoursePurchasePolicy.class);

        enrollmentPort =
                mock(EnrollmentPort.class);

        orderRepository =
                mock(OrderRepository.class);

        orderItemRepository =
                mock(OrderItemRepository.class);

        paymentRepository =
                mock(PaymentRepository.class);

        creditCommandUseCase =
                mock(CreditCommandUseCase.class);

        subscriptionRepository =
                mock(SubscriptionRepository.class);

        subscriptionPaymentRepository =
                mock(SubscriptionPaymentRepository.class);

        paymentCommandService = new PaymentCommandService(
                cartItemRepository,
                coursePurchasePolicy,
                enrollmentPort,
                orderRepository,
                orderItemRepository,
                paymentRepository,
                creditCommandUseCase,
                subscriptionRepository,
                subscriptionPaymentRepository
        );
    }

    @Test
    void checkout_cart_pays_selected_courses() {
        CartItem cartItem = createCartItem();
        CourseInfo courseInfo = createCourseInfo();
        Order savedOrder = createSavedCourseOrder();
        OrderItem savedOrderItem = createSavedCourseOrderItem();
        Payment savedPayment = createSavedCoursePayment();

        when(cartItemRepository.findAllSelectedByUserId(USER_ID))
                .thenReturn(List.of(cartItem));

        when(coursePurchasePolicy.validatePurchasable(
                USER_ID,
                COURSE_ID
        )).thenReturn(courseInfo);

        when(orderRepository.save(any(Order.class)))
                .thenReturn(savedOrder);

        when(orderItemRepository.save(any(OrderItem.class)))
                .thenReturn(savedOrderItem);

        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(savedPayment);

        when(creditCommandUseCase.useCredit(
                any(UseCreditCommand.class)
        )).thenReturn(
                new CreditBalanceResult(70_000L)
        );

        var result = paymentCommandService.checkout(
                new PaymentCheckoutCommand(
                        USER_ID,
                        PaymentPurchaseType.CART,
                        null,
                        null,
                        true
                )
        );

        assertThat(result.orderId())
                .isEqualTo(ORDER_ID);

        assertThat(result.orderNo())
                .isEqualTo("ORD-TEST");

        assertThat(result.paymentId())
                .isEqualTo(PAYMENT_ID);

        assertThat(result.purchaseType())
                .isEqualTo(PaymentPurchaseType.CART);

        assertThat(result.amount())
                .isEqualTo(COURSE_PRICE);

        assertThat(result.status())
                .isEqualTo("PAID");

        assertThat(result.creditBalance())
                .isEqualTo(70_000L);

        assertThat(result.courses())
                .hasSize(1);

        assertThat(result.subscription())
                .isNull();

        assertThat(result.courses().get(0).courseId())
                .isEqualTo(COURSE_ID);

        assertThat(result.courses().get(0).title())
                .isEqualTo("Spring Boot Basic");

        assertThat(result.courses().get(0).price())
                .isEqualTo(COURSE_PRICE);

        ArgumentCaptor<UseCreditCommand> creditCaptor =
                ArgumentCaptor.forClass(
                        UseCreditCommand.class
                );

        verify(creditCommandUseCase)
                .useCredit(creditCaptor.capture());

        assertThat(creditCaptor.getValue().userId())
                .isEqualTo(USER_ID);

        assertThat(creditCaptor.getValue().amount())
                .isEqualTo(COURSE_PRICE);

        verify(enrollmentPort)
                .enrollPaidCourse(
                        USER_ID,
                        COURSE_ID,
                        ORDER_ITEM_ID
                );

        verify(cartItemRepository)
                .deleteAllSelectedByUserId(USER_ID);

        verify(cartItemRepository, never())
                .deleteByUserIdAndCourseId(
                        anyLong(),
                        anyLong()
                );

        verifyNoInteractions(subscriptionRepository);
        verifyNoInteractions(subscriptionPaymentRepository);
    }

    @Test
    void checkout_course_pays_single_course() {
        CourseInfo courseInfo = createCourseInfo();
        Order savedOrder = createSavedCourseOrder();
        OrderItem savedOrderItem = createSavedCourseOrderItem();
        Payment savedPayment = createSavedCoursePayment();

        when(coursePurchasePolicy.validatePurchasable(
                USER_ID,
                COURSE_ID
        )).thenReturn(courseInfo);

        when(orderRepository.save(any(Order.class)))
                .thenReturn(savedOrder);

        when(orderItemRepository.save(any(OrderItem.class)))
                .thenReturn(savedOrderItem);

        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(savedPayment);

        when(creditCommandUseCase.useCredit(
                any(UseCreditCommand.class)
        )).thenReturn(
                new CreditBalanceResult(70_000L)
        );

        var result = paymentCommandService.checkout(
                new PaymentCheckoutCommand(
                        USER_ID,
                        PaymentPurchaseType.COURSE,
                        COURSE_ID,
                        null,
                        true
                )
        );

        assertThat(result.orderId())
                .isEqualTo(ORDER_ID);

        assertThat(result.paymentId())
                .isEqualTo(PAYMENT_ID);

        assertThat(result.purchaseType())
                .isEqualTo(PaymentPurchaseType.COURSE);

        assertThat(result.amount())
                .isEqualTo(COURSE_PRICE);

        assertThat(result.creditBalance())
                .isEqualTo(70_000L);

        assertThat(result.courses())
                .hasSize(1);

        assertThat(result.subscription())
                .isNull();

        verify(enrollmentPort)
                .enrollPaidCourse(
                        USER_ID,
                        COURSE_ID,
                        ORDER_ITEM_ID
                );

        verify(cartItemRepository)
                .deleteByUserIdAndCourseId(
                        USER_ID,
                        COURSE_ID
                );

        verify(cartItemRepository, never())
                .deleteAllSelectedByUserId(anyLong());

        verifyNoInteractions(subscriptionRepository);
        verifyNoInteractions(subscriptionPaymentRepository);
    }

    @Test
    void checkout_subscription_creates_subscription_and_payment_history() {
        Order savedOrder = createSavedSubscriptionOrder();
        Subscription savedSubscription =
                createSavedSubscription();

        OrderItem savedOrderItem =
                createSavedSubscriptionOrderItem();

        Payment savedPayment =
                createSavedSubscriptionPayment();

        when(subscriptionRepository.findActiveByUserId(
                eq(USER_ID),
                any(LocalDateTime.class)
        )).thenReturn(Optional.empty());

        when(orderRepository.save(any(Order.class)))
                .thenReturn(savedOrder);

        when(subscriptionRepository.save(
                any(Subscription.class)
        )).thenReturn(savedSubscription);

        when(orderItemRepository.save(
                any(OrderItem.class)
        )).thenReturn(savedOrderItem);

        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(savedPayment);

        when(creditCommandUseCase.useCredit(
                any(UseCreditCommand.class)
        )).thenReturn(
                new CreditBalanceResult(90_000L)
        );

        when(subscriptionPaymentRepository.save(
                any(SubscriptionPayment.class)
        )).thenAnswer(invocation ->
                invocation.getArgument(0)
        );

        var result = paymentCommandService.checkout(
                new PaymentCheckoutCommand(
                        USER_ID,
                        PaymentPurchaseType.AI_SUBSCRIPTION,
                        null,
                        SubscriptionPlan.MONTHLY,
                        true
                )
        );

        assertThat(result.orderId())
                .isEqualTo(ORDER_ID);

        assertThat(result.paymentId())
                .isEqualTo(PAYMENT_ID);

        assertThat(result.purchaseType())
                .isEqualTo(
                        PaymentPurchaseType.AI_SUBSCRIPTION
                );

        assertThat(result.amount())
                .isEqualTo(MONTHLY_PRICE);

        assertThat(result.status())
                .isEqualTo("PAID");

        assertThat(result.creditBalance())
                .isEqualTo(90_000L);

        assertThat(result.courses())
                .isEmpty();

        assertThat(result.subscription())
                .isNotNull();

        assertThat(result.subscription().subscriptionId())
                .isEqualTo(SUBSCRIPTION_ID);

        assertThat(result.subscription().planCode())
                .isEqualTo("MONTHLY");

        assertThat(result.subscription().planName())
                .isEqualTo("1개월 플랜");

        assertThat(result.subscription().status())
                .isEqualTo("ACTIVE");

        ArgumentCaptor<OrderItem> orderItemCaptor =
                ArgumentCaptor.forClass(OrderItem.class);

        verify(orderItemRepository)
                .save(orderItemCaptor.capture());

        OrderItem capturedOrderItem =
                orderItemCaptor.getValue();

        assertThat(capturedOrderItem.getItemType())
                .isEqualTo(
                        OrderItemType.AI_SUBSCRIPTION
                );

        assertThat(capturedOrderItem.getItemId())
                .isEqualTo(SUBSCRIPTION_ID);

        assertThat(capturedOrderItem.getCourseId())
                .isNull();

        assertThat(capturedOrderItem.getCourseTitle())
                .isEqualTo("1개월 플랜");

        ArgumentCaptor<UseCreditCommand> creditCaptor =
                ArgumentCaptor.forClass(
                        UseCreditCommand.class
                );

        verify(creditCommandUseCase)
                .useCredit(creditCaptor.capture());

        assertThat(creditCaptor.getValue().amount())
                .isEqualTo(MONTHLY_PRICE);

        ArgumentCaptor<SubscriptionPayment>
                subscriptionPaymentCaptor =
                ArgumentCaptor.forClass(
                        SubscriptionPayment.class
                );

        verify(subscriptionPaymentRepository)
                .save(
                        subscriptionPaymentCaptor.capture()
                );

        SubscriptionPayment capturedPayment =
                subscriptionPaymentCaptor.getValue();

        assertThat(capturedPayment.getSubscriptionId())
                .isEqualTo(SUBSCRIPTION_ID);

        assertThat(capturedPayment.getOrderId())
                .isEqualTo(ORDER_ID);

        assertThat(capturedPayment.getPaymentId())
                .isEqualTo(PAYMENT_ID);

        assertThat(capturedPayment.getUserId())
                .isEqualTo(USER_ID);

        assertThat(capturedPayment.getOrderNo())
                .isEqualTo("ORD-SUBSCRIPTION-TEST");

        assertThat(capturedPayment.getPlan())
                .isEqualTo(SubscriptionPlan.MONTHLY);

        assertThat(capturedPayment.getAmount())
                .isEqualTo(MONTHLY_PRICE);

        verifyNoInteractions(enrollmentPort);

        verify(cartItemRepository, never())
                .deleteAllSelectedByUserId(anyLong());

        verify(cartItemRepository, never())
                .deleteByUserIdAndCourseId(
                        anyLong(),
                        anyLong()
                );
    }

    @Test
    void checkout_subscription_rejects_active_subscription() {
        when(subscriptionRepository.findActiveByUserId(
                eq(USER_ID),
                any(LocalDateTime.class)
        )).thenReturn(
                Optional.of(createSavedSubscription())
        );

        BusinessException exception =
                catchThrowableOfType(
                        () -> paymentCommandService.checkout(
                                new PaymentCheckoutCommand(
                                        USER_ID,
                                        PaymentPurchaseType
                                                .AI_SUBSCRIPTION,
                                        null,
                                        SubscriptionPlan.MONTHLY,
                                        true
                                )
                        ),
                        BusinessException.class
                );

        assertThat(exception.getErrorCode())
                .isEqualTo(
                        ErrorCode.SUBSCRIPTION_ALREADY_ACTIVE
                );

        verify(subscriptionRepository)
                .findActiveByUserId(
                        eq(USER_ID),
                        any(LocalDateTime.class)
                );

        verifyNoInteractions(orderRepository);
        verifyNoInteractions(orderItemRepository);
        verifyNoInteractions(paymentRepository);
        verifyNoInteractions(creditCommandUseCase);
        verifyNoInteractions(subscriptionPaymentRepository);
        verifyNoInteractions(enrollmentPort);
    }

    @Test
    void checkout_subscription_requires_plan() {
        BusinessException exception =
                catchThrowableOfType(
                        () -> paymentCommandService.checkout(
                                new PaymentCheckoutCommand(
                                        USER_ID,
                                        PaymentPurchaseType
                                                .AI_SUBSCRIPTION,
                                        null,
                                        null,
                                        true
                                )
                        ),
                        BusinessException.class
                );

        assertThat(exception.getErrorCode())
                .isEqualTo(
                        ErrorCode
                                .PAYMENT_SUBSCRIPTION_PLAN_REQUIRED
                );

        verifyNoInteractions(subscriptionRepository);
        verifyNoInteractions(subscriptionPaymentRepository);
        verifyNoInteractions(orderRepository);
        verifyNoInteractions(paymentRepository);
        verifyNoInteractions(creditCommandUseCase);
    }

    @Test
    void checkout_subscription_rejects_course_id() {
        BusinessException exception =
                catchThrowableOfType(
                        () -> paymentCommandService.checkout(
                                new PaymentCheckoutCommand(
                                        USER_ID,
                                        PaymentPurchaseType
                                                .AI_SUBSCRIPTION,
                                        COURSE_ID,
                                        SubscriptionPlan.MONTHLY,
                                        true
                                )
                        ),
                        BusinessException.class
                );

        assertThat(exception.getErrorCode())
                .isEqualTo(
                        ErrorCode
                                .PAYMENT_SUBSCRIPTION_COURSE_ID_NOT_ALLOWED
                );

        verifyNoInteractions(subscriptionRepository);
        verifyNoInteractions(subscriptionPaymentRepository);
        verifyNoInteractions(orderRepository);
        verifyNoInteractions(paymentRepository);
        verifyNoInteractions(creditCommandUseCase);
    }

    @Test
    void checkout_course_rejects_subscription_plan() {
        BusinessException exception =
                catchThrowableOfType(
                        () -> paymentCommandService.checkout(
                                new PaymentCheckoutCommand(
                                        USER_ID,
                                        PaymentPurchaseType.COURSE,
                                        COURSE_ID,
                                        SubscriptionPlan.MONTHLY,
                                        true
                                )
                        ),
                        BusinessException.class
                );

        assertThat(exception.getErrorCode())
                .isEqualTo(
                        ErrorCode.PAYMENT_PLAN_NOT_ALLOWED
                );

        verifyNoInteractions(coursePurchasePolicy);
        verifyNoInteractions(orderRepository);
        verifyNoInteractions(paymentRepository);
        verifyNoInteractions(creditCommandUseCase);
    }

    @Test
    void checkout_cart_rejects_subscription_plan() {
        BusinessException exception =
                catchThrowableOfType(
                        () -> paymentCommandService.checkout(
                                new PaymentCheckoutCommand(
                                        USER_ID,
                                        PaymentPurchaseType.CART,
                                        null,
                                        SubscriptionPlan.MONTHLY,
                                        true
                                )
                        ),
                        BusinessException.class
                );

        assertThat(exception.getErrorCode())
                .isEqualTo(
                        ErrorCode.PAYMENT_PLAN_NOT_ALLOWED
                );

        verifyNoInteractions(cartItemRepository);
        verifyNoInteractions(orderRepository);
        verifyNoInteractions(paymentRepository);
        verifyNoInteractions(creditCommandUseCase);
    }

    @Test
    void checkout_cart_rejects_empty_selection() {
        when(cartItemRepository.findAllSelectedByUserId(
                USER_ID
        )).thenReturn(List.of());

        BusinessException exception =
                catchThrowableOfType(
                        () -> paymentCommandService.checkout(
                                new PaymentCheckoutCommand(
                                        USER_ID,
                                        PaymentPurchaseType.CART,
                                        null,
                                        null,
                                        true
                                )
                        ),
                        BusinessException.class
                );

        assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.CART_EMPTY_SELECTION);

        verifyNoInteractions(coursePurchasePolicy);
        verifyNoInteractions(orderRepository);
        verifyNoInteractions(orderItemRepository);
        verifyNoInteractions(paymentRepository);
        verifyNoInteractions(creditCommandUseCase);
        verifyNoInteractions(enrollmentPort);
        verifyNoInteractions(subscriptionRepository);
        verifyNoInteractions(subscriptionPaymentRepository);
    }

    @Test
    void checkout_does_not_complete_course_when_credit_is_insufficient() {
        CourseInfo courseInfo = createCourseInfo();

        when(coursePurchasePolicy.validatePurchasable(
                USER_ID,
                COURSE_ID
        )).thenReturn(courseInfo);

        when(orderRepository.save(any(Order.class)))
                .thenReturn(createSavedCourseOrder());

        when(orderItemRepository.save(any(OrderItem.class)))
                .thenReturn(createSavedCourseOrderItem());

        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(createSavedCoursePayment());

        when(creditCommandUseCase.useCredit(
                any(UseCreditCommand.class)
        )).thenThrow(
                new BusinessException(
                        ErrorCode.CREDIT_INSUFFICIENT_BALANCE
                )
        );

        BusinessException exception =
                catchThrowableOfType(
                        () -> paymentCommandService.checkout(
                                new PaymentCheckoutCommand(
                                        USER_ID,
                                        PaymentPurchaseType.COURSE,
                                        COURSE_ID,
                                        null,
                                        true
                                )
                        ),
                        BusinessException.class
                );

        assertThat(exception.getErrorCode())
                .isEqualTo(
                        ErrorCode.CREDIT_INSUFFICIENT_BALANCE
                );

        verify(enrollmentPort, never())
                .enrollPaidCourse(
                        anyLong(),
                        anyLong(),
                        anyLong()
                );

        verify(cartItemRepository, never())
                .deleteByUserIdAndCourseId(
                        anyLong(),
                        anyLong()
                );
    }

    @Test
    void checkout_does_not_create_subscription_payment_when_credit_is_insufficient() {
        when(subscriptionRepository.findActiveByUserId(
                eq(USER_ID),
                any(LocalDateTime.class)
        )).thenReturn(Optional.empty());

        when(orderRepository.save(any(Order.class)))
                .thenReturn(createSavedSubscriptionOrder());

        when(subscriptionRepository.save(
                any(Subscription.class)
        )).thenReturn(createSavedSubscription());

        when(orderItemRepository.save(any(OrderItem.class)))
                .thenReturn(
                        createSavedSubscriptionOrderItem()
                );

        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(
                        createSavedSubscriptionPayment()
                );

        when(creditCommandUseCase.useCredit(
                any(UseCreditCommand.class)
        )).thenThrow(
                new BusinessException(
                        ErrorCode.CREDIT_INSUFFICIENT_BALANCE
                )
        );

        BusinessException exception =
                catchThrowableOfType(
                        () -> paymentCommandService.checkout(
                                new PaymentCheckoutCommand(
                                        USER_ID,
                                        PaymentPurchaseType
                                                .AI_SUBSCRIPTION,
                                        null,
                                        SubscriptionPlan.MONTHLY,
                                        true
                                )
                        ),
                        BusinessException.class
                );

        assertThat(exception.getErrorCode())
                .isEqualTo(
                        ErrorCode.CREDIT_INSUFFICIENT_BALANCE
                );

        verify(subscriptionPaymentRepository, never())
                .save(any(SubscriptionPayment.class));

        verifyNoInteractions(enrollmentPort);
    }

    @Test
    void checkout_requires_agreement() {
        BusinessException exception =
                catchThrowableOfType(
                        () -> paymentCommandService.checkout(
                                new PaymentCheckoutCommand(
                                        USER_ID,
                                        PaymentPurchaseType.COURSE,
                                        COURSE_ID,
                                        null,
                                        false
                                )
                        ),
                        BusinessException.class
                );

        assertThat(exception.getErrorCode())
                .isEqualTo(
                        ErrorCode.PAYMENT_AGREEMENT_REQUIRED
                );

        verifyNoInteractions(cartItemRepository);
        verifyNoInteractions(coursePurchasePolicy);
        verifyNoInteractions(orderRepository);
        verifyNoInteractions(orderItemRepository);
        verifyNoInteractions(paymentRepository);
        verifyNoInteractions(creditCommandUseCase);
        verifyNoInteractions(subscriptionRepository);
        verifyNoInteractions(subscriptionPaymentRepository);
    }

    @Test
    void checkout_requires_purchase_type() {
        BusinessException exception =
                catchThrowableOfType(
                        () -> paymentCommandService.checkout(
                                new PaymentCheckoutCommand(
                                        USER_ID,
                                        null,
                                        COURSE_ID,
                                        null,
                                        true
                                )
                        ),
                        BusinessException.class
                );

        assertThat(exception.getErrorCode())
                .isEqualTo(
                        ErrorCode
                                .PAYMENT_INVALID_CHECKOUT_REQUEST
                );

        verifyNoInteractions(cartItemRepository);
        verifyNoInteractions(coursePurchasePolicy);
        verifyNoInteractions(orderRepository);
        verifyNoInteractions(orderItemRepository);
        verifyNoInteractions(paymentRepository);
        verifyNoInteractions(creditCommandUseCase);
        verifyNoInteractions(subscriptionRepository);
        verifyNoInteractions(subscriptionPaymentRepository);
    }

    @Test
    void checkout_course_requires_positive_course_id() {
        BusinessException nullIdException =
                catchThrowableOfType(
                        () -> paymentCommandService.checkout(
                                new PaymentCheckoutCommand(
                                        USER_ID,
                                        PaymentPurchaseType.COURSE,
                                        null,
                                        null,
                                        true
                                )
                        ),
                        BusinessException.class
                );

        assertThat(nullIdException.getErrorCode())
                .isEqualTo(
                        ErrorCode.PAYMENT_COURSE_ID_REQUIRED
                );

        BusinessException zeroIdException =
                catchThrowableOfType(
                        () -> paymentCommandService.checkout(
                                new PaymentCheckoutCommand(
                                        USER_ID,
                                        PaymentPurchaseType.COURSE,
                                        0L,
                                        null,
                                        true
                                )
                        ),
                        BusinessException.class
                );

        assertThat(zeroIdException.getErrorCode())
                .isEqualTo(
                        ErrorCode.PAYMENT_COURSE_ID_REQUIRED
                );
    }

    @Test
    void checkout_cart_rejects_course_id() {
        BusinessException exception =
                catchThrowableOfType(
                        () -> paymentCommandService.checkout(
                                new PaymentCheckoutCommand(
                                        USER_ID,
                                        PaymentPurchaseType.CART,
                                        COURSE_ID,
                                        null,
                                        true
                                )
                        ),
                        BusinessException.class
                );

        assertThat(exception.getErrorCode())
                .isEqualTo(
                        ErrorCode
                                .PAYMENT_CART_COURSE_ID_NOT_ALLOWED
                );

        verifyNoInteractions(cartItemRepository);
        verifyNoInteractions(orderRepository);
        verifyNoInteractions(paymentRepository);
    }

    private CartItem createCartItem() {
        return CartItem.restore(
                10L,
                USER_ID,
                COURSE_ID,
                COURSE_PRICE,
                true,
                LocalDateTime.now()
        );
    }

    private CourseInfo createCourseInfo() {
        return new CourseInfo(
                COURSE_ID,
                "Spring Boot Basic",
                COURSE_PRICE,
                "thumbnail.png",
                "Instructor",
                true
        );
    }

    private Order createSavedCourseOrder() {
        return Order.restore(
                ORDER_ID,
                "ORD-TEST",
                COURSE_PRICE,
                0L,
                COURSE_PRICE,
                OrderStatus.PAID,
                LocalDateTime.now(),
                USER_ID
        );
    }

    private Order createSavedSubscriptionOrder() {
        return Order.restore(
                ORDER_ID,
                "ORD-SUBSCRIPTION-TEST",
                MONTHLY_PRICE,
                0L,
                MONTHLY_PRICE,
                OrderStatus.PAID,
                LocalDateTime.now(),
                USER_ID
        );
    }

    private OrderItem createSavedCourseOrderItem() {
        return OrderItem.restore(
                ORDER_ITEM_ID,
                "Spring Boot Basic",
                COURSE_PRICE,
                0L,
                COURSE_PRICE,
                ORDER_ID,
                COURSE_ID
        );
    }

    private OrderItem createSavedSubscriptionOrderItem() {
        return OrderItem.restore(
                SUBSCRIPTION_ORDER_ITEM_ID,
                OrderItemType.AI_SUBSCRIPTION,
                SUBSCRIPTION_ID,
                "1개월 플랜",
                MONTHLY_PRICE,
                0L,
                MONTHLY_PRICE,
                ORDER_ID,
                null
        );
    }

    private Payment createSavedCoursePayment() {
        LocalDateTime now = LocalDateTime.now();

        return Payment.restore(
                PAYMENT_ID,
                COURSE_PRICE,
                PaymentStatus.PAID,
                now,
                now,
                ORDER_ID,
                USER_ID
        );
    }

    private Payment createSavedSubscriptionPayment() {
        LocalDateTime now = LocalDateTime.now();

        return Payment.restore(
                PAYMENT_ID,
                MONTHLY_PRICE,
                PaymentStatus.PAID,
                now,
                now,
                ORDER_ID,
                USER_ID
        );
    }

    private Subscription createSavedSubscription() {
        LocalDateTime startedAt = LocalDateTime.now();
        LocalDateTime expiredAt =
                startedAt.plusMonths(1);

        return Subscription.restore(
                SUBSCRIPTION_ID,
                SubscriptionPlan.MONTHLY,
                SubscriptionStatus.ACTIVE,
                MONTHLY_PRICE,
                startedAt,
                expiredAt,
                expiredAt,
                true,
                USER_ID
        );
    }
}