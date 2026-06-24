package com.sashimi.payment.infrastructure.persistence;

import com.sashimi.payment.domain.model.PaymentIdempotency;
import com.sashimi.payment.domain.model.PaymentIdempotencyStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "payment_idempotencies",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_payment_idempotencies_user_key",
                columnNames = {"user_id", "idempotency_key"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentIdempotencyJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_idempotency_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "idempotency_key", nullable = false, length = 100)
    private String idempotencyKey;

    @Column(name = "request_fingerprint", nullable = false, length = 200)
    private String requestFingerprint;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentIdempotencyStatus status;

    @Lob
    @Column(name = "result_json")
    private String resultJson;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static PaymentIdempotencyJpaEntity from(
            PaymentIdempotency idempotency
    ) {
        PaymentIdempotencyJpaEntity entity =
                new PaymentIdempotencyJpaEntity();

        entity.id = idempotency.getId();
        entity.userId = idempotency.getUserId();
        entity.idempotencyKey =
                idempotency.getIdempotencyKey();
        entity.requestFingerprint =
                idempotency.getRequestFingerprint();
        entity.status = idempotency.getStatus();
        entity.resultJson = idempotency.getResultJson();
        entity.createdAt = idempotency.getCreatedAt();
        entity.updatedAt = idempotency.getUpdatedAt();

        return entity;
    }

    public PaymentIdempotency toDomain() {
        return PaymentIdempotency.restore(
                id,
                userId,
                idempotencyKey,
                requestFingerprint,
                status,
                resultJson,
                createdAt,
                updatedAt
        );
    }
}