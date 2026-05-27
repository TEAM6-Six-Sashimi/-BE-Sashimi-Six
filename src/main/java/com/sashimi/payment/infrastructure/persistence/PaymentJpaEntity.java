package com.sashimi.payment.infrastructure.persistence;

import com.sashimi.payment.domain.model.Payment;
import com.sashimi.payment.domain.model.PaymentStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "payments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_payment_order",
                        columnNames = {"order_id"}
                )
        }
)
public class PaymentJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    protected PaymentJpaEntity() {
    }

    private PaymentJpaEntity(BigDecimal amount, PaymentStatus status, LocalDateTime paidAt,
                             LocalDateTime createdAt, Long orderId, Long userId) {
        this.amount = amount;
        this.status = status;
        this.paidAt = paidAt;
        this.createdAt = createdAt;
        this.orderId = orderId;
        this.userId = userId;
    }

    public static PaymentJpaEntity from(Payment payment) {
        return new PaymentJpaEntity(payment.getAmount(), payment.getStatus(), payment.getPaidAt(),
                payment.getCreatedAt(), payment.getOrderId(), payment.getUserId());
    }

    public Payment toDomain() {
        return Payment.restore(id, amount, status, paidAt, createdAt, orderId, userId);
    }
}