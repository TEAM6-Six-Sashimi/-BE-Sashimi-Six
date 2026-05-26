package com.sashimi.credit.application.usecase;

import com.sashimi.credit.application.command.ChargeCreditCommand;
import com.sashimi.credit.application.command.CreateInitialCreditCommand;
import com.sashimi.credit.application.command.GrantReferralSignupRewardCommand;
import com.sashimi.credit.application.command.UseCreditCommand;
import com.sashimi.credit.application.result.CreditBalanceResult;

public interface CreditCommandUseCase {

    void createInitialCredit(CreateInitialCreditCommand command);

    void grantReferralSignupRewards(GrantReferralSignupRewardCommand command);

    CreditBalanceResult chargeCredit(ChargeCreditCommand command);

    CreditBalanceResult useCredit(UseCreditCommand command);
}