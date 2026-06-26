package com.sashimi.payment.application.service.checkout;

import com.sashimi.cart.domain.model.CartItem;
import com.sashimi.cart.domain.repository.CartItemRepository;
import com.sashimi.credit.application.command.UseCreditCommand;
import com.sashimi.credit.application.result.CreditBalanceResult;
import com.sashimi.credit.application.usecase.CreditCommandUseCase;
import com.sashimi.enrollment.application.port.EnrollmentPort;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.order.application.policy.CoursePurchasePolicy;
import com.sashimi.order.domain.model.OrderItem;
import com.sashimi.payment.application.command.PaymentCheckoutCommand;
import com.sashimi.payment.application.command.PaymentPurchaseType;
import com.sashimi.payment.application.usecase.PaymentCommandUseCase.PaidCourse;
import com.sashimi.payment.application.usecase.PaymentCommandUseCase.PaymentResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartCheckoutProcessor implements PaymentCheckoutProcessor {

    private final CartItemRepository cartItemRepository;
    private final CoursePurchasePolicy coursePurchasePolicy;
    private final EnrollmentPort enrollmentPort;
    private final CreditCommandUseCase creditCommandUseCase;
    private final OrderPaymentWriter orderPaymentWriter;

    @Override
    public PaymentPurchaseType supports() {
        return PaymentPurchaseType.CART;
    }

    @Override
    public PaymentResult checkout(
            PaymentCheckoutCommand command
    ) {
        validate(command);

        List<CartItem> cartItems =
                cartItemRepository.findAllSelectedByUserId(
                        command.userId()
                );

        if (cartItems.isEmpty()) {
            throw new BusinessException(
                    ErrorCode.CART_EMPTY_SELECTION
            );
        }

        List<CheckoutCourse> courses = cartItems.stream()
                .map(item -> coursePurchasePolicy.validatePurchasable(
                        command.userId(),
                        item.getCourseId()
                ))
                .map(course -> new CheckoutCourse(
                        course.courseId(),
                        course.title(),
                        course.price()
                ))
                .toList();

        OrderPaymentWriter.CourseOrderPayment saved =
                orderPaymentWriter.saveCoursePayment(
                        command.userId(),
                        courses
                );

        CreditBalanceResult credit =
                creditCommandUseCase.useCredit(
                        new UseCreditCommand(
                                command.userId(),
                                saved.payment().getAmount()
                        )
                );

        for (OrderItem orderItem : saved.orderItems()) {
            enrollmentPort.enrollPaidCourse(
                    command.userId(),
                    orderItem.getCourseId(),
                    orderItem.getId()
            );
        }

        cartItemRepository.deleteAllSelectedByUserId(
                command.userId()
        );

        log.info(
                "장바구니 결제 완료 - userId={}, orderId={}, paymentId={}, amount={}, courseCount={}",
                command.userId(),
                saved.order().getId(),
                saved.payment().getId(),
                saved.payment().getAmount(),
                saved.orderItems().size()
        );

        return new PaymentResult(
                saved.order().getId(),
                saved.order().getOrderNo(),
                saved.payment().getId(),
                PaymentPurchaseType.CART,
                saved.payment().getAmount(),
                saved.payment().getStatus().name(),
                credit.balance(),
                saved.orderItems().stream()
                        .map(item -> new PaidCourse(
                                item.getCourseId(),
                                item.getCourseTitle(),
                                item.getFinalPrice()
                        ))
                        .toList(),
                null
        );
    }

    private void validate(
            PaymentCheckoutCommand command
    ) {
        if (command.courseId() != null) {
            throw new BusinessException(
                    ErrorCode.PAYMENT_CART_COURSE_ID_NOT_ALLOWED
            );
        }

        if (command.planCode() != null) {
            throw new BusinessException(
                    ErrorCode.PAYMENT_PLAN_NOT_ALLOWED
            );
        }
    }
}