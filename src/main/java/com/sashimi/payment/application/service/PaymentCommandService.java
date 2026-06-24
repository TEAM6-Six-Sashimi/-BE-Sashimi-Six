package com.sashimi.payment.application.service;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.payment.application.command.PaymentCheckoutCommand;
import com.sashimi.payment.application.service
        .PaymentIdempotencyTransactionService
        .ExistingRequestResolution;
import com.sashimi.payment.application.usecase.PaymentCommandUseCase;
import com.sashimi.payment.domain.model.PaymentIdempotency;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentCommandService
        implements PaymentCommandUseCase {

    private final PaymentIdempotencyTransactionService
            idempotencyTransactionService;

    private final PaymentCheckoutTransactionService
            checkoutTransactionService;

    private final PaymentResultJsonCodec paymentResultJsonCodec;

    @Override
    public PaymentResult checkout(
            PaymentCheckoutCommand command
    ) {
        validateIdempotencyKey(command.idempotencyKey());

        String fingerprint = createFingerprint(command);

        Long idempotencyId;

        try {
            PaymentIdempotency created =
                    idempotencyTransactionService.createProcessing(
                            command.userId(),
                            command.idempotencyKey(),
                            fingerprint
                    );

            idempotencyId = created.getId();
        } catch (DataIntegrityViolationException e) {
            ExistingRequestResolution resolution =
                    idempotencyTransactionService.resolveExisting(
                            command.userId(),
                            command.idempotencyKey(),
                            fingerprint
                    );

            switch (resolution.status()) {
                case COMPLETED -> {
                    return paymentResultJsonCodec.deserialize(
                            resolution.resultJson()
                    );
                }

                case PROCESSING -> throw new BusinessException(
                        ErrorCode.PAYMENT_IDEMPOTENCY_PROCESSING
                );

                case FAILED -> throw new BusinessException(
                        ErrorCode.PAYMENT_IDEMPOTENCY_FAILED
                );

                case RESTARTED ->
                        idempotencyId =
                                resolution.idempotencyId();

                default -> throw new BusinessException(
                        ErrorCode
                                .PAYMENT_IDEMPOTENCY_RESULT_INVALID
                );
            }
        }

        try {
            return checkoutTransactionService.execute(
                    command,
                    idempotencyId
            );
        } catch (RuntimeException e) {
            idempotencyTransactionService.markFailed(
                    idempotencyId
            );

            throw e;
        }
    }

    private void validateIdempotencyKey(String key) {
        if (key == null
                || key.isBlank()
                || key.length() > 100) {
            throw new BusinessException(
                    ErrorCode.PAYMENT_IDEMPOTENCY_KEY_INVALID
            );
        }
    }

    private String createFingerprint(
            PaymentCheckoutCommand command
    ) {
        return command.purchaseType()
                + ":"
                + valueOf(command.courseId())
                + ":"
                + valueOf(command.planCode());
    }

    private String valueOf(Object value) {
        return value == null
                ? "-"
                : value.toString();
    }
}