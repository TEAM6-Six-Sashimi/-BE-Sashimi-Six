package com.sashimi.payment.domain.model;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.Objects;

public class PaymentIdempotency {

    private final Long id;
    private final Long userId;
    private final String idempotencyKey;
    private final String requestFingerprint;
    private PaymentIdempotencyStatus status;
    private String resultJson;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private PaymentIdempotency(
            Long id, Long userId, String idempotencyKey,
            String requestFingerprint, PaymentIdempotencyStatus status,
            String resultJson, LocalDateTime createdAt, LocalDateTime updatedAt
    ) {
        if (userId == null || idempotencyKey == null || idempotencyKey.isBlank()
                || requestFingerprint == null || requestFingerprint.isBlank()
                || status == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        this.id = id;
        this.userId = userId;
        this.idempotencyKey = idempotencyKey;
        this.requestFingerprint = requestFingerprint;
        this.status = status;
        this.resultJson = resultJson;
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
        this.updatedAt = updatedAt == null ? LocalDateTime.now() : updatedAt;
    }

    public static PaymentIdempotency processing(
            Long userId, String key, String fingerprint
    ) {
        LocalDateTime now = LocalDateTime.now();
        return new PaymentIdempotency(
                null, userId, key, fingerprint,
                PaymentIdempotencyStatus.PROCESSING, null, now, now
        );
    }

    public static PaymentIdempotency restore(
            Long id, Long userId, String key, String fingerprint,
            PaymentIdempotencyStatus status, String resultJson,
            LocalDateTime createdAt, LocalDateTime updatedAt
    ) {
        return new PaymentIdempotency(
                id, userId, key, fingerprint, status,
                resultJson, createdAt, updatedAt
        );
    }

    public void validateFingerprint(String fingerprint) {
        if (!Objects.equals(requestFingerprint, fingerprint)) {
            throw new BusinessException(
                    ErrorCode.PAYMENT_IDEMPOTENCY_KEY_CONFLICT);
        }
    }

    public void restartProcessing() {
        if (status != PaymentIdempotencyStatus.PROCESSING) {
            throw new BusinessException(
                    ErrorCode.PAYMENT_IDEMPOTENCY_RESULT_INVALID
            );
        }

        resultJson = null;
        updatedAt = LocalDateTime.now();
    }

    public void complete(String resultJson) {
        if (status != PaymentIdempotencyStatus.PROCESSING
                || resultJson == null || resultJson.isBlank()) {
            throw new BusinessException(
                    ErrorCode.PAYMENT_IDEMPOTENCY_RESULT_INVALID);
        }

        status = PaymentIdempotencyStatus.COMPLETED;
        this.resultJson = resultJson;
        updatedAt = LocalDateTime.now();
    }

    public void fail() {
        if (status == PaymentIdempotencyStatus.PROCESSING) {
            status = PaymentIdempotencyStatus.FAILED;
            updatedAt = LocalDateTime.now();
        }
    }

    public boolean isProcessingExpired(LocalDateTime now, long timeoutMinutes) {
        if (!isProcessing()) {return false;
        }

        return updatedAt.isBefore(
                now.minusMinutes(timeoutMinutes)
        );
    }

    public boolean isProcessing() { return status == PaymentIdempotencyStatus.PROCESSING; }
    public boolean isCompleted() { return status == PaymentIdempotencyStatus.COMPLETED; }
    public boolean isFailed() { return status == PaymentIdempotencyStatus.FAILED; }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public String getRequestFingerprint() { return requestFingerprint; }
    public PaymentIdempotencyStatus getStatus() { return status; }
    public String getResultJson() { return resultJson; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}