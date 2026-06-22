package com.sashimi.payment.application.service;

import com.sashimi.cart.application.port.CourseInfo;
import com.sashimi.cart.domain.model.CartItem;
import com.sashimi.cart.domain.repository.CartItemRepository;
import com.sashimi.credit.application.usecase.CreditQueryUseCase;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.order.application.policy.CoursePurchasePolicy;
import com.sashimi.order.domain.repository.OrderItemRepository;
import com.sashimi.payment.application.usecase.PaymentQueryUseCase;
import com.sashimi.order.domain.model.Order;
import com.sashimi.payment.domain.model.Payment;
import com.sashimi.order.domain.repository.OrderRepository;
import com.sashimi.payment.domain.repository.PaymentRepository;
import com.sashimi.order.domain.model.OrderItem;
import com.sashimi.order.domain.model.OrderItemType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PaymentQueryService implements PaymentQueryUseCase {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final CoursePurchasePolicy coursePurchasePolicy;
    private final CreditQueryUseCase creditQueryUseCase;
    private final OrderItemRepository orderItemRepository;

    public PaymentQueryService(PaymentRepository paymentRepository, OrderRepository orderRepository, CartItemRepository cartItemRepository, CoursePurchasePolicy coursePurchasePolicy, CreditQueryUseCase creditQueryUseCase, OrderItemRepository orderItemRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.coursePurchasePolicy = coursePurchasePolicy;
        this.creditQueryUseCase = creditQueryUseCase;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public PaymentHistory getPaymentHistory(Long userId) {
        List<PaymentHistoryItem> items =
                paymentRepository.findAllByUserId(userId)
                        .stream()
                        .filter(this::isCoursePayment)
                        .map(this::toHistoryItem)
                        .toList();

        return new PaymentHistory(items);
    }

    private boolean isCoursePayment(Payment payment) {
        return orderItemRepository
                .findAllByOrderId(payment.getOrderId())
                .stream()
                .anyMatch(orderItem ->
                        orderItem.getItemType()
                                == OrderItemType.COURSE
                );
    }

    private PaymentHistoryItem toHistoryItem(Payment payment) {
        Order order = orderRepository.findById(payment.getOrderId())
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.PAYMENT_ORDER_NOT_FOUND)
                );

        List<PaymentHistoryCourse> courses =
                orderItemRepository.findAllByOrderId(order.getId())
                        .stream()
                        .filter(item -> item.getItemType() == OrderItemType.COURSE)
                        .map(this::toHistoryCourse)
                        .toList();

        return new PaymentHistoryItem(
                payment.getId(),
                order.getId(),
                order.getOrderNo(),
                payment.getAmount(),
                payment.getStatus().name(),
                order.getStatus().name(),
                payment.getPaidAt(),
                payment.getCreatedAt(),
                courses
        );
    }

    private PaymentHistoryCourse toHistoryCourse(OrderItem item) {
        return new PaymentHistoryCourse(
                item.getCourseId(),
                item.getCourseTitle(),
                item.getFinalPrice()
        );
    }

    @Override
    public PaymentPreview getCoursePreview(Long userId, Long courseId) {
        CourseInfo course = coursePurchasePolicy.validatePurchasable(userId, courseId);
        return createPreview("COURSE", List.of(toPreviewCourse(course)), userId);
    }

    @Override
    public PaymentPreview getCartPreview(Long userId) {
        List<CartItem> items = cartItemRepository.findAllSelectedByUserId(userId);

        if (items.isEmpty()) {
            throw new BusinessException(ErrorCode.CART_EMPTY_SELECTION);
        }

        List<PaymentPreviewCourse> courses = items.stream()
                .map(item -> coursePurchasePolicy.validatePurchasable(
                        userId, item.getCourseId()
                ))
                .map(this::toPreviewCourse)
                .toList();

        return createPreview("CART", courses, userId);
    }

    private PaymentPreviewCourse toPreviewCourse(CourseInfo course) {
        return new PaymentPreviewCourse(
                course.courseId(),
                course.title(),
                course.thumbnail(),
                course.instructorName(),
                course.price()
        );
    }

    private PaymentPreview createPreview(
            String purchaseType,
            List<PaymentPreviewCourse> courses,
            Long userId
    ) {
        long totalAmount = courses.stream()
                .mapToLong(PaymentPreviewCourse::price)
                .sum();

        long balance = creditQueryUseCase.getBalance(userId).balance();

        return new PaymentPreview(
                purchaseType,
                courses,
                totalAmount,
                balance,
                Math.max(balance - totalAmount, 0L),
                Math.max(totalAmount - balance, 0L),
                balance >= totalAmount
        );
    }
}