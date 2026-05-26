package com.sashimi.credit.presentation.api.response;

import com.sashimi.credit.application.result.CreditBalanceResult;

import java.math.BigDecimal;

public record CreditResponse(
        BigDecimal balance
) {
    public static CreditResponse from(CreditBalanceResult result) {
        return new CreditResponse(result.balance());
    }
}