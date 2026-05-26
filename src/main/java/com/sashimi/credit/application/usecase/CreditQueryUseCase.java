package com.sashimi.credit.application.usecase;

import com.sashimi.credit.application.result.CreditBalanceResult;

public interface CreditQueryUseCase {

    CreditBalanceResult getBalance(Long userId);
}