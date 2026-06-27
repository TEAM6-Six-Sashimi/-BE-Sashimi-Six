package com.sashimi.payment.application.service.checkout;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.order.domain.model.Order;
import com.sashimi.order.domain.model.OrderItem;
import com.sashimi.order.domain.repository.OrderItemRepository;
import com.sashimi.order.domain.repository.OrderRepository;
import com.sashimi.payment.domain.model.Payment;
import com.sashimi.payment.domain.repository.PaymentRepository;
import com.sashimi.subscription.domain.model.Subscription;
import com.sashimi.subscription.domain.model.SubscriptionPlan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderPaymentWriter {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;

    public CourseOrderPayment saveCoursePayment(
            Long userId,
            List<CheckoutCourse> courses
    ) {
        if (courses == null || courses.isEmpty()) {
            throw new BusinessException(
                    ErrorCode.PAYMENT_EMPTY_COURSE
            );
        }

        long totalAmount = courses.stream()
                .mapToLong(CheckoutCourse::price)
                .sum();

        Order order = orderRepository.save(
                Order.paid(
                        createOrderNo(),
                        totalAmount,
                        userId
                )
        );

        List<OrderItem> orderItems = courses.stream()
                .map(course -> orderItemRepository.save(
                        OrderItem.createCourse(
                                course.title(),
                                course.price(),
                                order.getId(),
                                course.courseId()
                        )
                ))
                .toList();

        Payment payment = paymentRepository.save(
                Payment.paid(
                        totalAmount,
                        order.getId(),
                        userId
                )
        );

        return new CourseOrderPayment(
                order,
                payment,
                orderItems
        );
    }

    public SubscriptionOrderPayment saveSubscriptionPayment(
            Long userId,
            Subscription subscription,
            SubscriptionPlan plan
    ) {
        Order order = orderRepository.save(
                Order.paid(
                        createOrderNo(),
                        plan.getPrice(),
                        userId
                )
        );

        OrderItem orderItem = orderItemRepository.save(
                OrderItem.createSubscription(
                        plan.getPlanName(),
                        plan.getPrice(),
                        order.getId(),
                        subscription.getId()
                )
        );

        Payment payment = paymentRepository.save(
                Payment.paid(
                        plan.getPrice(),
                        order.getId(),
                        userId
                )
        );

        return new SubscriptionOrderPayment(
                order,
                payment,
                orderItem
        );
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

    public record CourseOrderPayment(
            Order order,
            Payment payment,
            List<OrderItem> orderItems
    ) {
    }

    public record SubscriptionOrderPayment(
            Order order,
            Payment payment,
            OrderItem orderItem
    ) {
    }
}