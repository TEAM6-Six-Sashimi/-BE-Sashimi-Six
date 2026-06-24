package com.sashimi.payment.application.service;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.payment.domain.model.PaymentIdempotency;
import com.sashimi.payment.domain.repository.PaymentIdempotencyRepository;
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
            String fingerprint
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

        existing.validateFingerprint(fingerprint);

        if (existing.isCompleted()) {
            return ExistingRequestResolution.completed(
                    existing.getResultJson()
            );
        }

        if (existing.isFailed()) {
            return ExistingRequestResolution.failed();
        }

        LocalDateTime now = LocalDateTime.now();

        if (existing.isProcessingExpired(
                now,
                PROCESSING_TIMEOUT_MINUTES
        )) {
            existing.restartProcessing();

            PaymentIdempotency saved =
                    repository.save(existing);

            return ExistingRequestResolution.restarted(
                    saved.getId()
            );
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
        RESTARTED,
        FAILED
    }

    public record ExistingRequestResolution(
            ExistingRequestStatus status,
            Long idempotencyId,
            String resultJson
    ) {
        public static ExistingRequestResolution completed(
                String resultJson
        ) {
            return new ExistingRequestResolution(
                    ExistingRequestStatus.COMPLETED,
                    null,
                    resultJson
            );
        }

        public static ExistingRequestResolution processing() {
            return new ExistingRequestResolution(
                    ExistingRequestStatus.PROCESSING,
                    null,
                    null
            );
        }

        public static ExistingRequestResolution restarted(
                Long idempotencyId
        ) {
            return new ExistingRequestResolution(
                    ExistingRequestStatus.RESTARTED,
                    idempotencyId,
                    null
            );
        }

        public static ExistingRequestResolution failed() {
            return new ExistingRequestResolution(
                    ExistingRequestStatus.FAILED,
                    null,
                    null
            );
        }
    }
}