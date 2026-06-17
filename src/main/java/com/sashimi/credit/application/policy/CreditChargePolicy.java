package com.sashimi.credit.application.policy;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import org.springframework.stereotype.Component;

@Component
public class CreditChargePolicy {

    private static final long MIN_CHARGE_AMOUNT = 10_000L;
    private static final long CHARGE_AMOUNT_UNIT = 1_000L;

    public void validate(Long amount) {
        if (amount == null || amount <= 0) {
            throw new BusinessException(ErrorCode.CREDIT_INVALID_AMOUNT);
        }

        if (amount < MIN_CHARGE_AMOUNT) {
            throw new BusinessException(ErrorCode.CREDIT_CHARGE_AMOUNT_TOO_SMALL);
        }

        if (amount % CHARGE_AMOUNT_UNIT != 0) {
            throw new BusinessException(ErrorCode.CREDIT_CHARGE_AMOUNT_UNIT_INVALID);
        }
    }
}