package com.sashimi.verification.infrastructure.outbox;

public enum OutboxStatus {
    PENDING, PROCESSING, SENT, FAILED
}
