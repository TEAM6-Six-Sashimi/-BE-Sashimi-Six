package com.sashimi.credit.presentation.api.response;

import java.math.BigDecimal;

public record CreditResponse(
        BigDecimal balance
) {
}