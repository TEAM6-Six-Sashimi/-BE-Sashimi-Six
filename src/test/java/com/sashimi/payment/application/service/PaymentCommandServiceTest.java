package com.sashimi.payment.application.service;

import com.sashimi.order.application.policy.CoursePurchasePolicy;
import com.sashimi.cart.application.port.CourseInfo;
import com.sashimi.cart.domain.model.CartItem;
import com.sashimi.cart.domain.repository.CartItemRepository;
import com.sashimi.credit.application.command.UseCreditCommand;
import com.sashimi.credit.application.usecase.CreditCommandUseCase;
import com.sashimi.enrollment.application.port.EnrollmentPort;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.order.domain.model.OrderStatus;
import com.sashimi.payment.application.command.CheckoutCartCommand;
import com.sashimi.payment.application.command.PayCourseCommand;
import com.sashimi.order.domain.model.Order;
import com.sashimi.order.domain.model.OrderItem;
import com.sashimi.payment.domain.model.Payment;
import com.sashimi.order.domain.repository.OrderItemRepository;
import com.sashimi.order.domain.repository.OrderRepository;
import com.sashimi.payment.domain.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.*;

class PaymentCommandServiceTest {

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
    void 장바구니_선택_강의를_결제하면_크레딧을_차감하고_수강등록_후_선택항목_삭제() {
        CartItem cartItem = CartItem.restore(
                10L,
                1L,
                100L,
                30000L,
                true,
                LocalDateTime.now()
        );

        CourseInfo courseInfo = new CourseInfo(
                100L,
                "Spring Boot Basic",
                30000L,
                "thumbnail.png",
                "Instructor",
                true
        );

        Order savedOrder = Order.restore(
                1L,
                "ORD-TEST",
                30000L,
                0L,
                30000L,
                OrderStatus.PAID,
                LocalDateTime.now(),
                1L
        );

        OrderItem savedOrderItem = OrderItem.restore(
                11L,
                "Spring Boot Basic",
                30000L,
                0L,
                30000L,
                1L,
                100L
        );

        Payment savedPayment = Payment.restore(
                20L,
                30000L,
                com.sashimi.payment.domain.model.PaymentStatus.PAID,
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L,
                1L
        );

        when(cartItemRepository.findAllSelectedByUserId(1L))
                .thenReturn(List.of(cartItem));
        when(coursePurchasePolicy.validatePurchasable(1L, 100L))
                .thenReturn(courseInfo);
        when(orderRepository.save(any(Order.class)))
                .thenReturn(savedOrder);
        when(orderItemRepository.save(any(OrderItem.class)))
                .thenReturn(savedOrderItem);
        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(savedPayment);

        var result = paymentCommandService.checkoutCart(new CheckoutCartCommand(1L));

        assertThat(result.orderId()).isEqualTo(1L);
        assertThat(result.paymentId()).isEqualTo(20L);
        assertThat(result.amount()).isEqualTo(30000L);
        assertThat(result.courses()).hasSize(1);
        assertThat(result.courses().get(0).courseId()).isEqualTo(100L);

        ArgumentCaptor<UseCreditCommand> creditCaptor =
                ArgumentCaptor.forClass(UseCreditCommand.class);

        verify(creditCommandUseCase).useCredit(creditCaptor.capture());
        assertThat(creditCaptor.getValue().userId()).isEqualTo(1L);
        assertThat(creditCaptor.getValue().amount()).isEqualTo(30000L);

        verify(enrollmentPort).enrollPaidCourse(1L, 100L, 11L);
        verify(cartItemRepository).deleteAllSelectedByUserId(1L);
        verify(cartItemRepository, never()).deleteByUserIdAndCourseId(anyLong(), anyLong());
    }

    @Test
    void 단일_강의를_바로_결제_시_수강등록_후_같은_강의_장바구니_항목_삭제() {
        CourseInfo courseInfo = new CourseInfo(
                100L,
                "Spring Boot Basic",
                30000L,
                "thumbnail.png",
                "Instructor",
                true
        );

        Order savedOrder = Order.restore(
                1L,
                "ORD-TEST",
                30000L,
                0L,
                30000L,
                OrderStatus.PAID,
                LocalDateTime.now(),
                1L
        );

        OrderItem savedOrderItem = OrderItem.restore(
                11L,
                "Spring Boot Basic",
                30000L,
                0L,
                30000L,
                1L,
                100L
        );

        Payment savedPayment = Payment.restore(
                20L,
                30000L,
                com.sashimi.payment.domain.model.PaymentStatus.PAID,
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L,
                1L
        );

        when(coursePurchasePolicy.validatePurchasable(1L, 100L))
                .thenReturn(courseInfo);
        when(orderRepository.save(any(Order.class)))
                .thenReturn(savedOrder);
        when(orderItemRepository.save(any(OrderItem.class)))
                .thenReturn(savedOrderItem);
        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(savedPayment);

        var result = paymentCommandService.payCourse(new PayCourseCommand(1L, 100L));

        assertThat(result.orderId()).isEqualTo(1L);
        assertThat(result.paymentId()).isEqualTo(20L);
        assertThat(result.courses()).hasSize(1);

        verify(creditCommandUseCase).useCredit(any(UseCreditCommand.class));
        verify(enrollmentPort).enrollPaidCourse(1L, 100L, 11L);
        verify(cartItemRepository).deleteByUserIdAndCourseId(1L, 100L);
        verify(cartItemRepository, never()).deleteAllSelectedByUserId(anyLong());
    }

    @Test
    void 선택된_장바구니_항목이_없으면_결제_진행_X() {
        when(cartItemRepository.findAllSelectedByUserId(1L))
                .thenReturn(List.of());

        BusinessException exception = catchThrowableOfType(
                () -> paymentCommandService.checkoutCart(new CheckoutCartCommand(1L)),
                BusinessException.class
        );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CART_EMPTY_SELECTION);

        verifyNoInteractions(coursePurchasePolicy);
        verifyNoInteractions(orderRepository);
        verifyNoInteractions(orderItemRepository);
        verifyNoInteractions(paymentRepository);
        verifyNoInteractions(creditCommandUseCase);
        verifyNoInteractions(enrollmentPort);
    }

    @Test
    void 크레딧이_부족하면_수강등록과_장바구니_삭제_X() {
        CourseInfo courseInfo = new CourseInfo(
                100L,
                "Spring Boot Basic",
                30000L,
                "thumbnail.png",
                "Instructor",
                true
        );

        Order savedOrder = Order.restore(
                1L,
                "ORD-TEST",
                30000L,
                0L,
                30000L,
                OrderStatus.PAID,
                LocalDateTime.now(),
                1L
        );

        OrderItem savedOrderItem = OrderItem.restore(
                11L,
                "Spring Boot Basic",
                30000L,
                0L,
                30000L,
                1L,
                100L
        );

        Payment savedPayment = Payment.restore(
                20L,
                30000L,
                com.sashimi.payment.domain.model.PaymentStatus.PAID,
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L,
                1L
        );

        when(coursePurchasePolicy.validatePurchasable(1L, 100L))
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
                () -> paymentCommandService.payCourse(new PayCourseCommand(1L, 100L)),
                BusinessException.class
        );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.CREDIT_INSUFFICIENT_BALANCE);

        verify(enrollmentPort, never()).enrollPaidCourse(anyLong(), anyLong(), anyLong());
        verify(cartItemRepository, never()).deleteByUserIdAndCourseId(anyLong(), anyLong());
        verify(cartItemRepository, never()).deleteAllSelectedByUserId(anyLong());
    }
}