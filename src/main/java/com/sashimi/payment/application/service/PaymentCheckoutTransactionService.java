package com.sashimi.payment.application.service;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.payment.application.command.PaymentCheckoutCommand;
import com.sashimi.payment.application.event.PaymentCompletedEvent;
import com.sashimi.payment.application.service.checkout.PaymentCheckoutProcessor;
import com.sashimi.payment.application.usecase.PaymentCommandUseCase.PaymentResult;
import com.sashimi.payment.domain.model.PaymentIdempotency;
import com.sashimi.payment.domain.repository.PaymentIdempotencyRepository;
import com.sashimi.payment.application.command.PaymentPurchaseType;
import com.sashimi.payment.metric.PaymentMetrics;
import com.sashimi.user.domain.model.User;
import com.sashimi.user.domain.repository.UserRepository;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PaymentCheckoutTransactionService {

    private final Map<PaymentPurchaseType, PaymentCheckoutProcessor> processors;
    private final PaymentIdempotencyRepository paymentIdempotencyRepository;
    private final PaymentResultJsonCodec paymentResultJsonCodec;
    private final PaymentMetrics paymentMetrics;
    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository userRepository;

    public PaymentCheckoutTransactionService(
            List<PaymentCheckoutProcessor> processors,
            PaymentIdempotencyRepository paymentIdempotencyRepository,
            PaymentResultJsonCodec paymentResultJsonCodec,
            PaymentMetrics paymentMetrics,
            ApplicationEventPublisher eventPublisher,
            UserRepository userRepository
    ) {
        this.processors = new EnumMap<>(PaymentPurchaseType.class);
        for (PaymentCheckoutProcessor processor : processors) {
            this.processors.put(processor.supports(), processor
            );
        }
        this.paymentIdempotencyRepository = paymentIdempotencyRepository;
        this.paymentResultJsonCodec = paymentResultJsonCodec;
        this.paymentMetrics = paymentMetrics;
        this.eventPublisher = eventPublisher;
        this.userRepository = userRepository;
    }

    public PaymentResult execute(
            PaymentCheckoutCommand command,
            Long idempotencyId
    ) {
        Timer.Sample sample = paymentMetrics.startTimer();

        try {
            validateCheckoutRequest(command);

            PaymentCheckoutProcessor processor =
                    processors.get(command.purchaseType());

            if (processor == null) {
                throw new BusinessException(
                        ErrorCode.PAYMENT_INVALID_CHECKOUT_REQUEST
                );
            }

            PaymentResult result = processor.checkout(command);

            User user = userRepository.findById(command.userId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
            eventPublisher.publishEvent(new PaymentCompletedEvent(
                    command.userId(),
                    user.getEmail(),
                    user.getName(),
                    result
            ));

            completeIdempotency(
                    idempotencyId,
                    result
            );

            paymentMetrics.recordPaymentCompleted(
                    command.purchaseType()
            );

            paymentMetrics.recordPaymentProcessingSuccess(
                    sample,
                    command.purchaseType()
            );

            return result;
        } catch (RuntimeException e) {
            paymentMetrics.recordPaymentProcessingFailure(
                    sample,
                    command == null
                            ? null
                            : command.purchaseType()
            );

            throw e;
        }
    }

    private void validateCheckoutRequest(
            PaymentCheckoutCommand command
    ) {
        if (command == null
                || command.purchaseType() == null) {
            throw new BusinessException(
                    ErrorCode.PAYMENT_INVALID_CHECKOUT_REQUEST
            );
        }

        if (!Boolean.TRUE.equals(command.agreed())) {
            throw new BusinessException(
                    ErrorCode.PAYMENT_AGREEMENT_REQUIRED
            );
        }
    }

    private void completeIdempotency(
            Long idempotencyId,
            PaymentResult result
    ) {
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
    }
}