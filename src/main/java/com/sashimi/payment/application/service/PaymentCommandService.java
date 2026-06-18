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
import com.sashimi.payment.application.command.CheckoutCartCommand;
import com.sashimi.payment.application.command.PayCourseCommand;
import com.sashimi.payment.application.usecase.PaymentCommandUseCase;
import com.sashimi.order.domain.model.Order;
import com.sashimi.order.domain.model.OrderItem;
import com.sashimi.payment.domain.model.Payment;
import com.sashimi.order.domain.repository.OrderItemRepository;
import com.sashimi.order.domain.repository.OrderRepository;
import com.sashimi.payment.domain.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class PaymentCommandService implements PaymentCommandUseCase {

    private final CartItemRepository cartItemRepository;
    private final CoursePurchasePolicy coursePurchasePolicy;
    private final EnrollmentPort enrollmentPort;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final CreditCommandUseCase creditCommandUseCase;

    public PaymentCommandService(
            CartItemRepository cartItemRepository,
            CoursePurchasePolicy coursePurchasePolicy,
            EnrollmentPort enrollmentPort,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            PaymentRepository paymentRepository,
            CreditCommandUseCase creditCommandUseCase
    ) {
        this.cartItemRepository = cartItemRepository;
        this.coursePurchasePolicy = coursePurchasePolicy;
        this.enrollmentPort = enrollmentPort;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.creditCommandUseCase = creditCommandUseCase;
    }

    @Override
    public PaymentResult checkoutCart(CheckoutCartCommand command) {
        log.info("장바구니 결제 요청 - userId={}", command.userId());

        List<CartItem> cartItems = cartItemRepository.findAllSelectedByUserId(command.userId());

        if (cartItems.isEmpty()) {
            throw new BusinessException(ErrorCode.CART_EMPTY_SELECTION);
        }

        List<PaymentCourse> courses = cartItems.stream()
                .map(cartItem -> {
                    CourseInfo courseInfo = coursePurchasePolicy.validatePurchasable(
                            command.userId(),
                            cartItem.getCourseId()
                    );

                    return new PaymentCourse(
                            courseInfo.courseId(),
                            courseInfo.title(),
                            courseInfo.price()
                    );
                })
                .toList();

        return pay(command.userId(), courses, PaymentSource.CART);
    }

    @Override
    public PaymentResult payCourse(PayCourseCommand command) {
        log.info("단일 강의 결제 요청 - userId={}, courseId={}",
                command.userId(), command.courseId());

        CourseInfo courseInfo = coursePurchasePolicy.validatePurchasable(
                command.userId(),
                command.courseId()
        );

        PaymentResult result = pay(command.userId(), List.of(
                new PaymentCourse(courseInfo.courseId(), courseInfo.title(), courseInfo.price())
        ), PaymentSource.DIRECT);

        cartItemRepository.deleteByUserIdAndCourseId(
                command.userId(),
                command.courseId()
        );

        return result;
    }

    private PaymentResult pay(Long userId, List<PaymentCourse> courses, PaymentSource source) {
        if (courses == null || courses.isEmpty()) {
            throw new BusinessException(ErrorCode.PAYMENT_EMPTY_COURSE);
        }

        Long totalAmount = courses.stream()
                .mapToLong(PaymentCourse::price)
                .sum();

        Order order = orderRepository.save(Order.paid(createOrderNo(), totalAmount, userId));

        log.info("주문 생성 완료 - userId={}, orderId={}, orderNo={}, totalAmount={}",
                userId, order.getId(), order.getOrderNo(), totalAmount);

        List<OrderItem> orderItems = courses.stream()
                .map(course -> orderItemRepository.save(
                        OrderItem.createCourse(course.title(), course.price(), order.getId(), course.courseId())
                ))
                .toList();

        Payment payment = paymentRepository.save(Payment.paid(totalAmount, order.getId(), userId));

        completePayment(userId, payment, orderItems, source);

        log.info("결제 완료 처리 완료 - userId={}, orderId={}, paymentId={}, amount={}, courseCount={}",
                userId, order.getId(), payment.getId(), payment.getAmount(), orderItems.size());

        return new PaymentResult(
                order.getId(),
                order.getOrderNo(),
                payment.getId(),
                payment.getAmount(),
                payment.getStatus().name(),
                orderItems.stream()
                        .map(item -> new PaidCourse(item.getCourseId(), item.getCourseTitle(), item.getFinalPrice()))
                        .toList()
        );
    }

    private void completePayment(
            Long userId,
            Payment payment,
            List<OrderItem> orderItems,
            PaymentSource source
    ) {
        creditCommandUseCase.useCredit(
                new UseCreditCommand(userId, payment.getAmount())
        );

        for (OrderItem orderItem : orderItems) {
            enrollmentPort.enrollPaidCourse(
                    userId,
                    orderItem.getCourseId(),
                    orderItem.getId()
            );
        }

        if (source == PaymentSource.CART) {
            cartItemRepository.deleteAllSelectedByUserId(userId);
        }
    }

    private String createOrderNo() {
        return "ORD-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
                + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private enum PaymentSource {
        CART,
        DIRECT
    }

    private record PaymentCourse(Long courseId, String title, Long price) {
    }
}