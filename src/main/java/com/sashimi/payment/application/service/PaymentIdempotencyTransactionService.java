package com.sashimi.payment.application.service;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.payment.application.logging.PaymentAuditLogger;
import com.sashimi.payment.domain.model.PaymentIdempotency;
import com.sashimi.payment.domain.repository.PaymentIdempotencyRepository;
import com.sashimi.payment.application.command.PaymentPurchaseType;
import com.sashimi.payment.metric.PaymentMetrics;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentIdempotencyTransactionService {

    private static final long PROCESSING_TIMEOUT_MINUTES = 5L;

    private final PaymentIdempotencyRepository repository;
    private final PaymentMetrics paymentMetrics;
    private final PaymentAuditLogger paymentAuditLogger;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PaymentIdempotency createProcessing(
            Long userId,
            String key,
            String fingerprint
    ) {
        return repository.saveAndFlush(
                PaymentIdempotency.processing(
                        userId,
                        key,
                        fingerprint
                )
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ExistingRequestResolution resolveExisting(
            Long userId,
            String key,
            String fingerprint,
            PaymentPurchaseType purchaseType
    ) {
        PaymentIdempotency existing =
                repository
                        .findByUserIdAndIdempotencyKeyForUpdate(
                                userId,
                                key
                        )
                        .orElseThrow(() -> new BusinessException(
                                ErrorCode
                                        .PAYMENT_IDEMPOTENCY_RESULT_INVALID
                        ));

        try {
            existing.validateFingerprint(fingerprint);
        } catch (BusinessException e) {
            paymentMetrics.recordPaymentIdempotencyConflict(
                    purchaseType
            );

            paymentAuditLogger.idempotencyConflict(
                    userId, purchaseType);

            throw e;
        }

        if (existing.isCompleted()) {
            paymentMetrics.recordPaymentIdempotencyReused(
                    purchaseType);

            paymentAuditLogger.idempotencyReused(
                    userId, purchaseType);

            return ExistingRequestResolution.completed(existing.getResultJson());

        } if (existing.isFailed()) {
            return ExistingRequestResolution.failed();
        }
            LocalDateTime now = LocalDateTime.now();

        if (existing.isProcessingExpired(now, PROCESSING_TIMEOUT_MINUTES)) {
            existing.fail();
            repository.save(existing);
            return ExistingRequestResolution.failed();
        }

        return ExistingRequestResolution.processing();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(Long id) {
        PaymentIdempotency idempotency =
                repository.findByIdForUpdate(id)
                        .orElseThrow(() -> new BusinessException(
                                ErrorCode
                                        .PAYMENT_IDEMPOTENCY_RESULT_INVALID
                        ));

        idempotency.fail();
        repository.save(idempotency);
    }

    public enum ExistingRequestStatus {
        COMPLETED,
        PROCESSING,
        FAILED
    }

    public record ExistingRequestResolution(
            ExistingRequestStatus status,
            String resultJson
    ) {
        public static ExistingRequestResolution completed(
                String resultJson
        ) {
            return new ExistingRequestResolution(
                    ExistingRequestStatus.COMPLETED,
                    resultJson
            );
        }

        public static ExistingRequestResolution processing() {
            return new ExistingRequestResolution(
                    ExistingRequestStatus.PROCESSING,
                    null
            );
        }

        public static ExistingRequestResolution failed() {
            return new ExistingRequestResolution(
                    ExistingRequestStatus.FAILED,
                    null
            );
        }
    }
}