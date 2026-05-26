package com.sashimi.credit.application.result;

import java.math.BigDecimal;

public record CreditBalanceResult(
        BigDecimal balance
) {
}