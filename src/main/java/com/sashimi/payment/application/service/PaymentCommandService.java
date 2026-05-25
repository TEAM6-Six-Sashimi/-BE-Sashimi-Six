package com.sashimi.payment.application.service;

import com.sashimi.cart.application.port.CourseInfo;
import com.sashimi.cart.application.port.CoursePort;
import com.sashimi.cart.application.port.EnrollmentPort;
import com.sashimi.cart.domain.model.CartItem;
import com.sashimi.cart.domain.repository.CartItemRepository;
import com.sashimi.payment.application.command.CheckoutCartCommand;
import com.sashimi.payment.application.command.PayCourseCommand;
import com.sashimi.payment.application.usecase.PaymentCommandUseCase;
import com.sashimi.payment.domain.model.Order;
import com.sashimi.payment.domain.model.OrderItem;
import com.sashimi.payment.domain.model.Payment;
import com.sashimi.payment.domain.repository.OrderItemRepository;
import com.sashimi.payment.domain.repository.OrderRepository;
import com.sashimi.payment.domain.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
public class PaymentCommandService implements PaymentCommandUseCase {

    private final CartItemRepository cartItemRepository;
    private final CoursePort coursePort;
    private final EnrollmentPort enrollmentPort;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;

    public PaymentCommandService(CartItemRepository cartItemRepository, CoursePort coursePort,
                                 EnrollmentPort enrollmentPort, OrderRepository orderRepository,
                                 OrderItemRepository orderItemRepository, PaymentRepository paymentRepository) {
        this.cartItemRepository = cartItemRepository;
        this.coursePort = coursePort;
        this.enrollmentPort = enrollmentPort;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public PaymentResult checkoutCart(CheckoutCartCommand command) {
        List<CartItem> cartItems = cartItemRepository.findAllSelectedByUserId(command.userId());

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("결제할 강의를 선택해주세요.");
        }

        List<PaymentCourse> courses = cartItems.stream()
                .map(cartItem -> {
                    CourseInfo courseInfo = coursePort.getCourseInfo(cartItem.getCourseId());
                    return new PaymentCourse(courseInfo.courseId(), courseInfo.title(), cartItem.getPrice());
                })
                .toList();

        return pay(command.userId(), courses);
    }

    @Override
    public PaymentResult payCourse(PayCourseCommand command) {
        CourseInfo courseInfo = coursePort.getCourseInfo(command.courseId());

        if (!courseInfo.purchasable()) {
            throw new IllegalArgumentException("구매할 수 없는 강의입니다.");
        }

        if (enrollmentPort.isEnrolled(command.userId(), command.courseId())) {
            throw new IllegalArgumentException("이미 수강 중인 강의입니다.");
        }

        return pay(command.userId(), List.of(
                new PaymentCourse(courseInfo.courseId(), courseInfo.title(), courseInfo.price())
        ));
    }

    private PaymentResult pay(Long userId, List<PaymentCourse> courses) {
        BigDecimal totalAmount = courses.stream()
                .map(PaymentCourse::price)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = orderRepository.save(Order.paid(createOrderNo(), totalAmount, userId));

        List<OrderItem> orderItems = courses.stream()
                .map(course -> orderItemRepository.save(
                        OrderItem.create(course.title(), course.price(), order.getId(), course.courseId())
                ))
                .toList();

        Payment payment = paymentRepository.save(Payment.paid(totalAmount, order.getId(), userId));

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

    private String createOrderNo() {
        return "ORD-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }

    private record PaymentCourse(Long courseId, String title, BigDecimal price) {
    }
}