package com.sashimi.payment.application.service;

import com.sashimi.cart.application.port.CourseInfo;
import com.sashimi.cart.domain.model.CartItem;
import com.sashimi.cart.domain.repository.CartItemRepository;
import com.sashimi.credit.application.command.UseCreditCommand;
import com.sashimi.credit.application.usecase.CreditCommandUseCase;
import com.sashimi.enrollment.application.port.EnrollmentPort;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.order.application.policy.CoursePurchasePolicy;
import com.sashimi.order.domain.model.Order;
import com.sashimi.order.domain.model.OrderItem;
import com.sashimi.order.domain.model.OrderStatus;
import com.sashimi.order.domain.repository.OrderItemRepository;
import com.sashimi.order.domain.repository.OrderRepository;
import com.sashimi.payment.application.command.PaymentCheckoutCommand;
import com.sashimi.payment.application.command.PaymentPurchaseType;
import com.sashimi.payment.domain.model.Payment;
import com.sashimi.payment.domain.model.PaymentStatus;
import com.sashimi.payment.domain.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class PaymentCommandServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long COURSE_ID = 100L;
    private static final Long COURSE_PRICE = 30_000L;
    private static final Long ORDER_ID = 1L;
    private static final Long ORDER_ITEM_ID = 11L;
    private static final Long PAYMENT_ID = 20L;

    private CartItemRepository cartItemRepository;
    private CoursePurchasePolicy coursePurchasePolicy;
    private EnrollmentPort enrollmentPort;
    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;
    private PaymentRepository paymentRepository;
    private CreditCommandUseCase creditCommandUseCase;

    private PaymentCommandService paymentCommandService;

    @BeforeEach
    void setUp() {
        cartItemRepository = mock(CartItemRepository.class);
        coursePurchasePolicy = mock(CoursePurchasePolicy.class);
        enrollmentPort = mock(EnrollmentPort.class);
        orderRepository = mock(OrderRepository.class);
        orderItemRepository = mock(OrderItemRepository.class);
        paymentRepository = mock(PaymentRepository.class);
        creditCommandUseCase = mock(CreditCommandUseCase.class);

        paymentCommandService = new PaymentCommandService(
                cartItemRepository,
                coursePurchasePolicy,
                enrollmentPort,
                orderRepository,
                orderItemRepository,
                paymentRepository,
                creditCommandUseCase
        );
    }

    @Test
    void 장바구니에서_선택한_강의를_결제하면_크레딧을_차감하고_수강_등록한_뒤_선택_항목을_삭제한다() {
        CartItem cartItem = createCartItem();
        CourseInfo courseInfo = createCourseInfo();
        Order savedOrder = createSavedOrder();
        OrderItem savedOrderItem = createSavedOrderItem();
        Payment savedPayment = createSavedPayment();

        when(cartItemRepository.findAllSelectedByUserId(USER_ID))
                .thenReturn(List.of(cartItem));
        when(coursePurchasePolicy.validatePurchasable(USER_ID, COURSE_ID))
                .thenReturn(courseInfo);
        when(orderRepository.save(any(Order.class)))
                .thenReturn(savedOrder);
        when(orderItemRepository.save(any(OrderItem.class)))
                .thenReturn(savedOrderItem);
        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(savedPayment);

        var result = paymentCommandService.checkout(
                new PaymentCheckoutCommand(
                        USER_ID,
                        PaymentPurchaseType.CART,
                        null,
                        true
                )
        );

        assertThat(result.orderId()).isEqualTo(ORDER_ID);
        assertThat(result.orderNo()).isEqualTo("ORD-TEST");
        assertThat(result.paymentId()).isEqualTo(PAYMENT_ID);
        assertThat(result.amount()).isEqualTo(COURSE_PRICE);
        assertThat(result.status()).isEqualTo("PAID");
        assertThat(result.courses()).hasSize(1);
        assertThat(result.courses().get(0).courseId())
                .isEqualTo(COURSE_ID);
        assertThat(result.courses().get(0).title())
                .isEqualTo("Spring Boot Basic");
        assertThat(result.courses().get(0).price())
                .isEqualTo(COURSE_PRICE);

        ArgumentCaptor<UseCreditCommand> creditCaptor =
                ArgumentCaptor.forClass(UseCreditCommand.class);

        verify(creditCommandUseCase).useCredit(creditCaptor.capture());
        assertThat(creditCaptor.getValue().userId()).isEqualTo(USER_ID);
        assertThat(creditCaptor.getValue().amount()).isEqualTo(COURSE_PRICE);

        verify(coursePurchasePolicy)
                .validatePurchasable(USER_ID, COURSE_ID);
        verify(enrollmentPort)
                .enrollPaidCourse(USER_ID, COURSE_ID, ORDER_ITEM_ID);
        verify(cartItemRepository)
                .deleteAllSelectedByUserId(USER_ID);
        verify(cartItemRepository, never())
                .deleteByUserIdAndCourseId(anyLong(), anyLong());
    }

    @Test
    void 단일_강의를_결제하면_크레딧을_차감하고_수강_등록한_뒤_같은_강의를_장바구니에서_삭제한다() {
        CourseInfo courseInfo = createCourseInfo();
        Order savedOrder = createSavedOrder();
        OrderItem savedOrderItem = createSavedOrderItem();
        Payment savedPayment = createSavedPayment();

        when(coursePurchasePolicy.validatePurchasable(USER_ID, COURSE_ID))
                .thenReturn(courseInfo);
        when(orderRepository.save(any(Order.class)))
                .thenReturn(savedOrder);
        when(orderItemRepository.save(any(OrderItem.class)))
                .thenReturn(savedOrderItem);
        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(savedPayment);

        var result = paymentCommandService.checkout(
                new PaymentCheckoutCommand(
                        USER_ID,
                        PaymentPurchaseType.COURSE,
                        COURSE_ID,
                        true
                )
        );

        assertThat(result.orderId()).isEqualTo(ORDER_ID);
        assertThat(result.orderNo()).isEqualTo("ORD-TEST");
        assertThat(result.paymentId()).isEqualTo(PAYMENT_ID);
        assertThat(result.amount()).isEqualTo(COURSE_PRICE);
        assertThat(result.status()).isEqualTo("PAID");
        assertThat(result.courses()).hasSize(1);
        assertThat(result.courses().get(0).courseId())
                .isEqualTo(COURSE_ID);

        ArgumentCaptor<UseCreditCommand> creditCaptor =
                ArgumentCaptor.forClass(UseCreditCommand.class);

        verify(creditCommandUseCase).useCredit(creditCaptor.capture());
        assertThat(creditCaptor.getValue().userId()).isEqualTo(USER_ID);
        assertThat(creditCaptor.getValue().amount()).isEqualTo(COURSE_PRICE);

        verify(coursePurchasePolicy)
                .validatePurchasable(USER_ID, COURSE_ID);
        verify(enrollmentPort)
                .enrollPaidCourse(USER_ID, COURSE_ID, ORDER_ITEM_ID);
        verify(cartItemRepository)
                .deleteByUserIdAndCourseId(USER_ID, COURSE_ID);
        verify(cartItemRepository, never())
                .deleteAllSelectedByUserId(anyLong());
    }

    @Test
    void 선택한_장바구니_항목이_없으면_결제를_진행하지_않는다() {
        when(cartItemRepository.findAllSelectedByUserId(USER_ID))
                .thenReturn(List.of());

        BusinessException exception = catchThrowableOfType(
                () -> paymentCommandService.checkout(
                        new PaymentCheckoutCommand(
                                USER_ID,
                                PaymentPurchaseType.CART,
                                null,
                                true
                        )
                ),
                BusinessException.class
        );

        assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.CART_EMPTY_SELECTION);

        verify(cartItemRepository)
                .findAllSelectedByUserId(USER_ID);
        verifyNoInteractions(coursePurchasePolicy);
        verifyNoInteractions(orderRepository);
        verifyNoInteractions(orderItemRepository);
        verifyNoInteractions(paymentRepository);
        verifyNoInteractions(creditCommandUseCase);
        verifyNoInteractions(enrollmentPort);

        verify(cartItemRepository, never())
                .deleteAllSelectedByUserId(anyLong());
    }

    @Test
    void 크레딧이_부족하면_수강_등록과_장바구니_삭제를_진행하지_않는다() {
        CourseInfo courseInfo = createCourseInfo();
        Order savedOrder = createSavedOrder();
        OrderItem savedOrderItem = createSavedOrderItem();
        Payment savedPayment = createSavedPayment();

        when(coursePurchasePolicy.validatePurchasable(USER_ID, COURSE_ID))
                .thenReturn(courseInfo);
        when(orderRepository.save(any(Order.class)))
                .thenReturn(savedOrder);
        when(orderItemRepository.save(any(OrderItem.class)))
                .thenReturn(savedOrderItem);
        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(savedPayment);

        doThrow(new BusinessException(ErrorCode.CREDIT_INSUFFICIENT_BALANCE))
                .when(creditCommandUseCase)
                .useCredit(any(UseCreditCommand.class));

        BusinessException exception = catchThrowableOfType(
                () -> paymentCommandService.checkout(
                        new PaymentCheckoutCommand(
                                USER_ID,
                                PaymentPurchaseType.COURSE,
                                COURSE_ID,
                                true
                        )
                ),
                BusinessException.class
        );

        assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.CREDIT_INSUFFICIENT_BALANCE);

        verify(creditCommandUseCase)
                .useCredit(any(UseCreditCommand.class));
        verify(enrollmentPort, never())
                .enrollPaidCourse(anyLong(), anyLong(), anyLong());
        verify(cartItemRepository, never())
                .deleteByUserIdAndCourseId(anyLong(), anyLong());
        verify(cartItemRepository, never())
                .deleteAllSelectedByUserId(anyLong());
    }

    @Test
    void 결제에_동의하지_않으면_결제를_진행하지_않는다() {
        BusinessException exception = catchThrowableOfType(
                () -> paymentCommandService.checkout(
                        new PaymentCheckoutCommand(
                                USER_ID,
                                PaymentPurchaseType.COURSE,
                                COURSE_ID,
                                false
                        )
                ),
                BusinessException.class
        );

        assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.PAYMENT_AGREEMENT_REQUIRED);

        verifyNoInteractions(cartItemRepository);
        verifyNoInteractions(coursePurchasePolicy);
        verifyNoInteractions(orderRepository);
        verifyNoInteractions(orderItemRepository);
        verifyNoInteractions(paymentRepository);
        verifyNoInteractions(creditCommandUseCase);
        verifyNoInteractions(enrollmentPort);
    }

    @Test
    void 구매_유형이_없으면_결제를_진행하지_않는다() {
        BusinessException exception = catchThrowableOfType(
                () -> paymentCommandService.checkout(
                        new PaymentCheckoutCommand(
                                USER_ID,
                                null,
                                COURSE_ID,
                                true
                        )
                ),
                BusinessException.class
        );

        assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.PAYMENT_INVALID_CHECKOUT_REQUEST);

        verifyNoInteractions(cartItemRepository);
        verifyNoInteractions(coursePurchasePolicy);
        verifyNoInteractions(orderRepository);
        verifyNoInteractions(orderItemRepository);
        verifyNoInteractions(paymentRepository);
        verifyNoInteractions(creditCommandUseCase);
        verifyNoInteractions(enrollmentPort);
    }

    @Test
    void 단일_강의_결제에는_courseId가_필요하다() {
        BusinessException exception = catchThrowableOfType(
                () -> paymentCommandService.checkout(
                        new PaymentCheckoutCommand(
                                USER_ID,
                                PaymentPurchaseType.COURSE,
                                null,
                                true
                        )
                ),
                BusinessException.class
        );

        assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.PAYMENT_COURSE_ID_REQUIRED);

        verifyNoInteractions(cartItemRepository);
        verifyNoInteractions(coursePurchasePolicy);
        verifyNoInteractions(orderRepository);
        verifyNoInteractions(orderItemRepository);
        verifyNoInteractions(paymentRepository);
        verifyNoInteractions(creditCommandUseCase);
        verifyNoInteractions(enrollmentPort);
    }

    @Test
    void 단일_강의_결제의_courseId가_양수가_아니면_결제할_수_없다() {
        BusinessException exception = catchThrowableOfType(
                () -> paymentCommandService.checkout(
                        new PaymentCheckoutCommand(
                                USER_ID,
                                PaymentPurchaseType.COURSE,
                                0L,
                                true
                        )
                ),
                BusinessException.class
        );

        assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.PAYMENT_COURSE_ID_REQUIRED);

        verifyNoInteractions(cartItemRepository);
        verifyNoInteractions(coursePurchasePolicy);
        verifyNoInteractions(orderRepository);
        verifyNoInteractions(orderItemRepository);
        verifyNoInteractions(paymentRepository);
        verifyNoInteractions(creditCommandUseCase);
        verifyNoInteractions(enrollmentPort);
    }

    @Test
    void 장바구니_결제에는_courseId를_전달할_수_없다() {
        BusinessException exception = catchThrowableOfType(
                () -> paymentCommandService.checkout(
                        new PaymentCheckoutCommand(
                                USER_ID,
                                PaymentPurchaseType.CART,
                                COURSE_ID,
                                true
                        )
                ),
                BusinessException.class
        );

        assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.PAYMENT_CART_COURSE_ID_NOT_ALLOWED);

        verifyNoInteractions(cartItemRepository);
        verifyNoInteractions(coursePurchasePolicy);
        verifyNoInteractions(orderRepository);
        verifyNoInteractions(orderItemRepository);
        verifyNoInteractions(paymentRepository);
        verifyNoInteractions(creditCommandUseCase);
        verifyNoInteractions(enrollmentPort);
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

    private Order createSavedOrder() {
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

    private OrderItem createSavedOrderItem() {
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

    private Payment createSavedPayment() {
        return Payment.restore(
                PAYMENT_ID,
                COURSE_PRICE,
                PaymentStatus.PAID,
                LocalDateTime.now(),
                LocalDateTime.now(),
                ORDER_ID,
                USER_ID
        );
    }
}