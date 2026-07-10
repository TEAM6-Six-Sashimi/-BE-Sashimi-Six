package com.sashimi.payment.application.service.checkout;

import com.sashimi.cart.application.port.CourseInfo;
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
import com.sashimi.payment.application.logging.PaymentAuditLogger;
import com.sashimi.payment.application.usecase.PaymentCommandUseCase.PaidCourse;
import com.sashimi.payment.application.usecase.PaymentCommandUseCase.PaymentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class CourseCheckoutProcessor implements PaymentCheckoutProcessor {

    private final CoursePurchasePolicy coursePurchasePolicy;
    private final CartItemRepository cartItemRepository;
    private final EnrollmentPort enrollmentPort;
    private final CreditCommandUseCase creditCommandUseCase;
    private final OrderPaymentWriter orderPaymentWriter;
    private final PaymentAuditLogger paymentAuditLogger;

    @Override
    public PaymentPurchaseType supports() {
        return PaymentPurchaseType.COURSE;
    }

    @Override
    public PaymentResult checkout(
            PaymentCheckoutCommand command
    ) {
        validate(command);

        CourseInfo course =
                coursePurchasePolicy.validatePurchasable(
                        command.userId(),
                        command.courseId()
                );

        OrderPaymentWriter.CourseOrderPayment saved =
                orderPaymentWriter.saveCoursePayment(
                        command.userId(),
                        List.of(
                                new CheckoutCourse(
                                        course.courseId(),
                                        course.title(),
                                        course.price()
                                )
                        )
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

        cartItemRepository.deleteByUserIdAndCourseId(
                command.userId(),
                command.courseId()
        );

        paymentAuditLogger.coursePaymentCompleted(
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
                PaymentPurchaseType.COURSE,
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
        if (command.courseId() == null
                || command.courseId() <= 0) {
            throw new BusinessException(
                    ErrorCode.PAYMENT_COURSE_ID_REQUIRED
            );
        }

        if (command.planCode() != null) {
            throw new BusinessException(
                    ErrorCode.PAYMENT_PLAN_NOT_ALLOWED
            );
        }
    }
}