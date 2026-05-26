package com.sashimi.credit.domain.model;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;

import java.math.BigDecimal;

public class Credit {

    private Long id;
    private Long userId;
    private BigDecimal balance;

    public Credit(Long id, Long userId, BigDecimal balance) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        BigDecimal safeBalance = balance == null ? BigDecimal.ZERO : balance;

        if (safeBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.CREDIT_INVALID_AMOUNT);
        }

        this.id = id;
        this.userId = userId;
        this.balance = safeBalance;
    }

    public static Credit create(Long userId, BigDecimal initialBalance) {
        return new Credit(null, userId, initialBalance);
    }

    public void add(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.CREDIT_INVALID_AMOUNT);
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new BusinessException(ErrorCode.CREDIT_INSUFFICIENT_BALANCE);
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