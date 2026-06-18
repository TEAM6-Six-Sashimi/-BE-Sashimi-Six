package com.sashimi.credit.infrastructure.persistence;

import com.sashimi.credit.domain.model.CreditChargePayment;
import com.sashimi.credit.domain.model.CreditChargePaymentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "credit_charge_payments",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_credit_charge_payments_order_id", columnNames = "order_id"),
                @UniqueConstraint(name = "uq_credit_charge_payments_payment_key", columnNames = "payment_key")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreditChargePaymentJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "credit_charge_payment_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "order_id", nullable = false, length = 64)
    private String orderId;

    @Column(name = "payment_key", length = 200)
    private String paymentKey;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private CreditChargePaymentStatus status;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    public static CreditChargePaymentJpaEntity from(CreditChargePayment payment) {
        CreditChargePaymentJpaEntity entity = new CreditChargePaymentJpaEntity();
        entity.id = payment.getId();
        entity.userId = payment.getUserId();
        entity.orderId = payment.getOrderId();
        entity.paymentKey = payment.getPaymentKey();
        entity.amount = payment.getAmount();
        entity.status = payment.getStatus();
        entity.failureReason = payment.getFailureReason();
        entity.requestedAt = payment.getRequestedAt();
        entity.approvedAt = payment.getApprovedAt();
        return entity;
    }

    public CreditChargePayment toDomain() {
        return CreditChargePayment.restore(
                id,
                userId,
                orderId,
                paymentKey,
                amount,
                status,
                failureReason,
                requestedAt,
                approvedAt
        );
    }
}