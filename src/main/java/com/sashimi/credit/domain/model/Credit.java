package com.sashimi.credit.domain.model;

import java.math.BigDecimal;

public class Credit {

    private Long id;
    private Long userId;
    private BigDecimal balance;

    public Credit(Long id, Long userId, BigDecimal balance) {
        this.id = id;
        this.userId = userId;
        this.balance = balance == null ? BigDecimal.ZERO : balance;
    }

    public static Credit create(Long userId, BigDecimal initialBalance) {
        return new Credit(null, userId, initialBalance);
    }

    public void add(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("크레딧 지급 금액은 0보다 커야 합니다.");
        }

        this.balance = this.balance.add(amount);
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}