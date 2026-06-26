package com.sashimi.payment.application.service;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.payment.application.command.PaymentCheckoutCommand;
import com.sashimi.payment.application.service.checkout.PaymentCheckoutProcessor;
import com.sashimi.payment.application.usecase.PaymentCommandUseCase.PaymentResult;
import com.sashimi.payment.domain.model.PaymentIdempotency;
import com.sashimi.payment.domain.repository.PaymentIdempotencyRepository;
import com.sashimi.payment.application.command.PaymentPurchaseType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PaymentCheckoutTransactionService {

    private final Map<PaymentPurchaseType, PaymentCheckoutProcessor>
            processors;

    private final PaymentIdempotencyRepository paymentIdempotencyRepository;
    private final PaymentResultJsonCodec paymentResultJsonCodec;

    public PaymentCheckoutTransactionService(
            List<PaymentCheckoutProcessor> processors,
            PaymentIdempotencyRepository paymentIdempotencyRepository,
            PaymentResultJsonCodec paymentResultJsonCodec
    ) {
        this.processors = new EnumMap<>(
                PaymentPurchaseType.class
        );

        for (PaymentCheckoutProcessor processor : processors) {
            this.processors.put(
                    processor.supports(),
                    processor
            );
        }

        this.paymentIdempotencyRepository = paymentIdempotencyRepository;
        this.paymentResultJsonCodec = paymentResultJsonCodec;
    }

    public PaymentResult execute(
            PaymentCheckoutCommand command,
            Long idempotencyId
    ) {
        validateCheckoutRequest(command);

        PaymentCheckoutProcessor processor =
                processors.get(command.purchaseType());

        if (processor == null) {
            throw new BusinessException(
                    ErrorCode.PAYMENT_INVALID_CHECKOUT_REQUEST
            );
        }

        PaymentResult result = processor.checkout(command);

        completeIdempotency(
                idempotencyId,
                result
        );

        return result;
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