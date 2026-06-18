package com.sashimi.credit.application.usecase;

import com.sashimi.credit.application.command.ConfirmCreditChargeCommand;
import com.sashimi.credit.application.command.CreateInitialCreditCommand;
import com.sashimi.credit.application.command.GrantReferralSignupRewardCommand;
import com.sashimi.credit.application.command.ReadyCreditChargeCommand;
import com.sashimi.credit.application.command.UseCreditCommand;
import com.sashimi.credit.application.result.CreditChargeConfirmResult;
import com.sashimi.credit.application.result.CreditChargeReadyResult;
import com.sashimi.credit.application.result.CreditBalanceResult;

public interface CreditCommandUseCase {

    void createInitialCredit(CreateInitialCreditCommand command);

    void grantReferralSignupRewards(GrantReferralSignupRewardCommand command);

    CreditBalanceResult useCredit(UseCreditCommand command);

    CreditChargeReadyResult readyCreditCharge(ReadyCreditChargeCommand command);

    CreditChargeConfirmResult confirmCreditCharge(ConfirmCreditChargeCommand command);
}