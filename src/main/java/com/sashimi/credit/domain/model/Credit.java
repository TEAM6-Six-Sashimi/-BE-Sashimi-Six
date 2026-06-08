package com.sashimi.credit.domain.model;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;

public class Credit {

    private Long id;
    private Long userId;
    private Long balance;

    public Credit(Long id, Long userId, Long balance) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        Long safeBalance = balance == null ? 0L : balance;

        if (safeBalance < 0) {
            throw new BusinessException(ErrorCode.CREDIT_INVALID_AMOUNT);
        }

        this.id = id;
        this.userId = userId;
        this.balance = safeBalance;
    }

    public static Credit create(Long userId, Long initialBalance) {
        return new Credit(null, userId, initialBalance);
    }

    public void add(Long amount) {
        validatePositiveAmount(amount);
        this.balance += amount;
    }

    public void use(Long amount) {
        validatePositiveAmount(amount);

        if (this.balance < amount) {
            throw new BusinessException(ErrorCode.CREDIT_INSUFFICIENT_BALANCE);
        }

        this.balance -= amount;
    }

    private void validatePositiveAmount(Long amount) {
        if (amount == null || amount <= 0) {
            throw new BusinessException(ErrorCode.CREDIT_INVALID_AMOUNT);
        }
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getBalance() {
        return balance;
    }
}