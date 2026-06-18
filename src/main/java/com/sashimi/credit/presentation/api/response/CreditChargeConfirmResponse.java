package com.sashimi.credit.presentation.api.response;

import com.sashimi.credit.application.result.CreditChargeConfirmResult;

public record CreditChargeConfirmResponse(
        Long balance,
        String orderId,
        String paymentKey,
        Long chargedAmount
) {
    public static CreditChargeConfirmResponse from(CreditChargeConfirmResult result) {
        return new CreditChargeConfirmResponse(
                result.balance(),
                result.orderId(),
                result.paymentKey(),
                result.chargedAmount()
        );
    }
}