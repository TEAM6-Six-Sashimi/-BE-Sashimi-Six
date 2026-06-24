package com.sashimi.credit.domain.model;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;

import java.time.LocalDateTime;

public class CreditChargePayment {

    private Long id;
    private Long userId;
    private String orderId;
    private String paymentKey;
    private String paymentMethod;
    private Long amount;
    private CreditChargePaymentStatus status;
    private String failureReason;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;

    private CreditChargePayment(
            Long id,
            Long userId,
            String orderId,
            String paymentKey,
            String paymentMethod,
            Long amount,
            CreditChargePaymentStatus status,
            String failureReason,
            LocalDateTime requestedAt,
            LocalDateTime approvedAt
    ) {
        if (userId == null || orderId == null || orderId.isBlank()
                || amount == null || amount <= 0 || status == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        this.id = id;
        this.userId = userId;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.status = status;
        this.failureReason = failureReason;
        this.requestedAt = requestedAt == null ? LocalDateTime.now() : requestedAt;
        this.approvedAt = approvedAt;
    }

    public static CreditChargePayment ready(Long userId, String orderId, Long amount) {
        return new CreditChargePayment(
                null,
                userId,
                orderId,
                null,
                null,
                amount,
                CreditChargePaymentStatus.READY,
                null,
                LocalDateTime.now(),
                null
        );
    }

    public static CreditChargePayment restore(
            Long id,
            Long userId,
            String orderId,
            String paymentKey,
            String paymentMethod,
            Long amount,
            CreditChargePaymentStatus status,
            String failureReason,
            LocalDateTime requestedAt,
            LocalDateTime approvedAt
    ) {
        return new CreditChargePayment(
                id,
                userId,
                orderId,
                paymentKey,
                paymentMethod,
                amount,
                status,
                failureReason,
                requestedAt,
                approvedAt
        );
    }

    public void validateOwner(Long userId) {
        if (!this.userId.equals(userId)) {
            throw new BusinessException(ErrorCode.CREDIT_CHARGE_PAYMENT_FORBIDDEN);
        }
    }

    public void validateAmount(Long amount) {
        if (!this.amount.equals(amount)) {
            throw new BusinessException(ErrorCode.CREDIT_CHARGE_PAYMENT_AMOUNT_MISMATCH);
        }
    }

    public void validatePaymentKey(String paymentKey) {
        if (this.paymentKey == null
                || !this.paymentKey.equals(paymentKey)) {
            throw new BusinessException(
                    ErrorCode.CREDIT_EXTERNAL_PAYMENT_RESPONSE_MISMATCH
            );
        }
    }

    public void markDone(
            String paymentKey,
            String paymentMethod,
            LocalDateTime approvedAt
    ) {
        if (status != CreditChargePaymentStatus.READY) {
            throw new BusinessException(
                    ErrorCode.CREDIT_CHARGE_PAYMENT_ALREADY_PROCESSED
            );
        }

        if (paymentKey == null || paymentKey.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        this.paymentKey = paymentKey;
        this.paymentMethod = paymentMethod;
        this.status = CreditChargePaymentStatus.DONE;
        this.approvedAt = approvedAt == null
                ? LocalDateTime.now()
                : approvedAt;
    }

    public void markFailed(String failureReason) {
        if (status == CreditChargePaymentStatus.DONE) {
            throw new BusinessException(ErrorCode.CREDIT_CHARGE_PAYMENT_ALREADY_PROCESSED);
        }

        this.status = CreditChargePaymentStatus.FAILED;
        this.failureReason = failureReason;
    }

    public boolean isFailed() {
        return status == CreditChargePaymentStatus.FAILED;
    }

    public boolean isDone() {
        return status == CreditChargePaymentStatus.DONE;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public Long getAmount() {
        return amount;
    }

    public CreditChargePaymentStatus getStatus() {
        return status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public String getPaymentMethod() {return paymentMethod; }
}