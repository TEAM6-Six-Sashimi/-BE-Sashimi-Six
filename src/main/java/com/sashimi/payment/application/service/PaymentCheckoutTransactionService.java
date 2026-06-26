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
import com.sashimi.order.domain.repository.OrderItemRepository;
import com.sashimi.order.domain.repository.OrderRepository;
import com.sashimi.payment.application.command.PaymentCheckoutCommand;
import com.sashimi.payment.application.command.PaymentPurchaseType;
import com.sashimi.payment.domain.model.Payment;
import com.sashimi.payment.domain.repository.PaymentRepository;
import com.sashimi.subscription.domain.model.Subscription;
import com.sashimi.subscription.domain.model.SubscriptionPayment;
import com.sashimi.subscription.domain.model.SubscriptionPlan;
import com.sashimi.subscription.domain.repository.SubscriptionPaymentRepository;
import com.sashimi.subscription.domain.repository.SubscriptionRepository;
import com.sashimi.payment.application.usecase.PaymentCommandUseCase.PaymentResult;
import com.sashimi.payment.application.usecase.PaymentCommandUseCase.PaidCourse;
import com.sashimi.payment.application.usecase.PaymentCommandUseCase.PaidSubscription;
import com.sashimi.payment.domain.model.PaymentIdempotency;
import com.sashimi.payment.domain.repository.PaymentIdempotencyRepository;
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
public class PaymentCheckoutTransactionService {

    private final CartItemRepository cartItemRepository;
    private final CoursePurchasePolicy coursePurchasePolicy;
    private final EnrollmentPort enrollmentPort;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final CreditCommandUseCase creditCommandUseCase;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPaymentRepository subscriptionPaymentRepository;
    private final PaymentIdempotencyRepository paymentIdempotencyRepository;
    private final PaymentResultJsonCodec paymentResultJsonCodec;

    public PaymentCheckoutTransactionService(
            CartItemRepository cartItemRepository, CoursePurchasePolicy coursePurchasePolicy,
            EnrollmentPort enrollmentPort, OrderRepository orderRepository,
            OrderItemRepository orderItemRepository, PaymentRepository paymentRepository,
            CreditCommandUseCase creditCommandUseCase, SubscriptionRepository subscriptionRepository,
            SubscriptionPaymentRepository subscriptionPaymentRepository,PaymentIdempotencyRepository paymentIdempotencyRepository,
            PaymentResultJsonCodec paymentResultJsonCodec
    ) {
        this.cartItemRepository = cartItemRepository;
        this.coursePurchasePolicy = coursePurchasePolicy;
        this.enrollmentPort = enrollmentPort;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.creditCommandUseCase = creditCommandUseCase;
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionPaymentRepository = subscriptionPaymentRepository;
        this.paymentIdempotencyRepository = paymentIdempotencyRepository;
        this.paymentResultJsonCodec = paymentResultJsonCodec;
    }

    public PaymentResult execute(
            PaymentCheckoutCommand command,
            Long idempotencyId
    ) {
        validateCheckoutRequest(command);

        PaymentResult result = switch (command.purchaseType()) {
            case COURSE -> {
                validateCourseRequest(command);
                yield checkoutCourse(
                        command.userId(),
                        command.courseId()
                );
            }

            case CART -> {
                validateCartRequest(command);
                yield checkoutCart(command.userId());
            }

            case AI_SUBSCRIPTION -> {
                validateSubscriptionRequest(command);
                yield checkoutSubscription(
                        command.userId(),
                        command.planCode()
                );
            }
        };

        PaymentIdempotency idempotency =
                paymentIdempotencyRepository
                        .findByIdForUpdate(idempotencyId)
                        .orElseThrow(() -> new BusinessException(
                                ErrorCode.PAYMENT_IDEMPOTENCY_RESULT_INVALID
                        ));

        idempotency.complete(
                paymentResultJsonCodec.serialize(result)
        );

        paymentIdempotencyRepository.save(idempotency);

        return result;
    }

    private void validateCourseRequest(
            PaymentCheckoutCommand command
    ) {
        if (command.courseId() == null
                || command.courseId() <= 0) {
            throw new BusinessException(
                    ErrorCode.PAYMENT_COURSE_ID_REQUIRED);
        }

        if (command.planCode() != null) {
            throw new BusinessException(
                    ErrorCode.PAYMENT_PLAN_NOT_ALLOWED);
        }
    }

    private void validateCartRequest(
            PaymentCheckoutCommand command
    ) {
        if (command.courseId() != null) {
            throw new BusinessException(
                    ErrorCode.PAYMENT_CART_COURSE_ID_NOT_ALLOWED);
        }

        if (command.planCode() != null) {
            throw new BusinessException(
                    ErrorCode.PAYMENT_PLAN_NOT_ALLOWED);
        }
    }
    private void validateCheckoutRequest(
            PaymentCheckoutCommand command
    ) {
        if (!Boolean.TRUE.equals(command.agreed())) {
            throw new BusinessException(
                    ErrorCode.PAYMENT_AGREEMENT_REQUIRED
            );
        }

        if (command.purchaseType() == null) {
            throw new BusinessException(
                    ErrorCode.PAYMENT_INVALID_CHECKOUT_REQUEST
            );
        }
    }

    private void validateSubscriptionRequest(
            PaymentCheckoutCommand command
    ) {
        if (command.courseId() != null) {
            throw new BusinessException(
                    ErrorCode
                            .PAYMENT_SUBSCRIPTION_COURSE_ID_NOT_ALLOWED);
        }

        if (command.planCode() == null) {
            throw new BusinessException(
                    ErrorCode.PAYMENT_SUBSCRIPTION_PLAN_REQUIRED);
        }
    }

    private PaymentResult checkoutCourse(Long userId, Long courseId) {
        CourseInfo course =
                coursePurchasePolicy.validatePurchasable(
                        userId,
                        courseId);

        PaymentResult result = payCourses(
                userId,
                List.of(
                        new PaymentCourse(
                                course.courseId(),
                                course.title(),
                                course.price())
                ),
                PaymentSource.DIRECT);

        cartItemRepository.deleteByUserIdAndCourseId(userId, courseId);

        return result;
    }

    private PaymentResult checkoutCart(Long userId) {
        List<CartItem> cartItems =
                cartItemRepository.findAllSelectedByUserId(
                        userId);

        if (cartItems.isEmpty()) {
            throw new BusinessException(
                    ErrorCode.CART_EMPTY_SELECTION);
        }

        List<PaymentCourse> courses = cartItems.stream().map(item -> coursePurchasePolicy.validatePurchasable(
                userId, item.getCourseId()))
                .map(course -> new PaymentCourse(course.courseId(), course.title(), course.price()))
                .toList();
        return payCourses(userId, courses, PaymentSource.CART);
    }

    private PaymentResult payCourses(
            Long userId,
            List<PaymentCourse> courses,
            PaymentSource source
    ) {
        if (courses == null || courses.isEmpty()) {
            throw new BusinessException(
                    ErrorCode.PAYMENT_EMPTY_COURSE);
        }

        long totalAmount = courses.stream().mapToLong(PaymentCourse::price).sum();

        Order order = orderRepository.save(Order.paid(createOrderNo(), totalAmount, userId) );

        List<OrderItem> orderItems = courses.stream().map(course -> orderItemRepository.save(
                OrderItem.createCourse(course.title(), course.price(), order.getId(), course.courseId()))).toList();

        Payment payment = paymentRepository.save(Payment.paid(totalAmount, order.getId(), userId));

        long creditBalance = completeCoursePayment(userId, payment, orderItems, source);

        PaymentPurchaseType purchaseType =
                source == PaymentSource.CART
                        ? PaymentPurchaseType.CART
                        : PaymentPurchaseType.COURSE;

        log.info("강의 결제 완료 - userId={}, orderId={}, paymentId={}, amount={}, courseCount={}",
                userId,
                order.getId(),
                payment.getId(),
                payment.getAmount(),
                orderItems.size()
        );

        return new PaymentResult(order.getId(), order.getOrderNo(), payment.getId(), purchaseType, payment.getAmount(), payment.getStatus().name(), creditBalance, orderItems.stream()
                        .map(item ->
                                new PaidCourse(item.getCourseId(), item.getCourseTitle(), item.getFinalPrice())).toList(), null);
    }

    private long completeCoursePayment(
            Long userId,
            Payment payment,
            List<OrderItem> orderItems,
            PaymentSource source
    ) {
        CreditBalanceResult credit =
                creditCommandUseCase.useCredit(
                        new UseCreditCommand(
                                userId,
                                payment.getAmount()
                        )
                );

        for (OrderItem orderItem : orderItems) {
            enrollmentPort.enrollPaidCourse(
                    userId,
                    orderItem.getCourseId(),
                    orderItem.getId()
            );
        }

        if (source == PaymentSource.CART) {
            cartItemRepository
                    .deleteAllSelectedByUserId(userId);
        }

        return credit.balance();
    }

    private PaymentResult checkoutSubscription(Long userId, SubscriptionPlan plan) {
        LocalDateTime now = LocalDateTime.now();

        subscriptionRepository.findActiveByUserIdForUpdate(userId)
                .ifPresent(subscription -> {
                    if (subscription.isActive(now)) {
                        throw new BusinessException(
                                ErrorCode.SUBSCRIPTION_ALREADY_ACTIVE
                        );
                    }

                    subscriptionRepository.save(
                            subscription.expire()
                    );
                });

        Order order = orderRepository.save(Order.paid(createOrderNo(), plan.getPrice(), userId));

        Subscription subscription = subscriptionRepository.save(Subscription.start(userId, plan, now));

        orderItemRepository.save(
                OrderItem.createSubscription(plan.getPlanName(), plan.getPrice(), order.getId(), subscription.getId())
        );

        Payment payment = paymentRepository.save(
                Payment.paid(plan.getPrice(), order.getId(),userId)
        );

        CreditBalanceResult credit =
                creditCommandUseCase.useCredit(new UseCreditCommand(userId, payment.getAmount())
                );

        subscriptionPaymentRepository.save(
                SubscriptionPayment.initial(
                        subscription.getId(),
                        order.getId(),
                        payment.getId(),
                        userId,
                        order.getOrderNo(),
                        plan,
                        payment.getPaidAt()
                )
        );

        log.info(
                "AI 구독권 결제 완료 - userId={}, subscriptionId={}, orderId={}, paymentId={}, plan={}, amount={}",
                userId,
                subscription.getId(),
                order.getId(),
                payment.getId(),
                plan.name(),
                payment.getAmount()
        );

        return new PaymentResult(
                order.getId(),
                order.getOrderNo(),
                payment.getId(),
                PaymentPurchaseType.AI_SUBSCRIPTION,
                payment.getAmount(),
                payment.getStatus().name(),
                credit.balance(),
                List.of(),
                new PaidSubscription(
                        subscription.getId(),
                        plan.name(),
                        plan.getPlanName(),
                        subscription.getStatus().name(),
                        subscription.getStartedAt(),
                        subscription.getExpiredAt(),
                        subscription.getNextBillingAt()
                )
        );
    }

    private String createOrderNo() {
        return "ORD-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
                + "-"
                + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }

    private enum PaymentSource {CART, DIRECT}

    private record PaymentCourse(Long courseId, String title, Long price) {
    }
}