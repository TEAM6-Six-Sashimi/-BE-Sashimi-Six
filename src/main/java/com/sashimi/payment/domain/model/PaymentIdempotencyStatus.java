package com.sashimi.payment.domain.model;

public enum PaymentIdempotencyStatus {
    PROCESSING,
    COMPLETED,
    FAILED
}