package com.sashimi.credit.infrastructure.persistence;

import com.sashimi.credit.domain.model.Credit;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "credits")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreditJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "credit_id")
    private Long id;

    @Column(nullable = false)
    private Long balance;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    public static CreditJpaEntity from(Credit credit) {
        CreditJpaEntity entity = new CreditJpaEntity();
        entity.id = credit.getId();
        entity.userId = credit.getUserId();
        entity.balance = credit.getBalance();
        return entity;
    }

    public Credit toDomain() {
        return new Credit(id, userId, balance);
    }
}