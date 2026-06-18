package com.sashimi.credit.presentation.api.response;

import com.sashimi.credit.application.result.CreditChargeReadyResult;

public record CreditChargeReadyResponse(
        String orderId,
        String orderName,
        Long amount
) {
    public static CreditChargeReadyResponse from(CreditChargeReadyResult result) {
        return new CreditChargeReadyResponse(
                result.orderId(),
                result.orderName(),
                result.amount()
        );
    }
}