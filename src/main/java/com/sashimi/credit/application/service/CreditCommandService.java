package com.sashimi.credit.application.service;

import com.sashimi.credit.application.command.ChargeCreditCommand;
import com.sashimi.credit.application.command.CreateInitialCreditCommand;
import com.sashimi.credit.application.command.GrantReferralSignupRewardCommand;
import com.sashimi.credit.application.command.UseCreditCommand;
import com.sashimi.credit.application.result.CreditBalanceResult;
import com.sashimi.credit.application.usecase.CreditCommandUseCase;
import com.sashimi.credit.domain.model.Credit;
import com.sashimi.credit.domain.repository.CreditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class CreditCommandService implements CreditCommandUseCase {

    public static final BigDecimal NEW_USER_REFERRAL_REWARD = BigDecimal.valueOf(5000);
    public static final BigDecimal REFERRER_REWARD = BigDecimal.valueOf(1000);

    private final CreditRepository creditRepository;

    @Override
    public void createInitialCredit(CreateInitialCreditCommand command) {
        BigDecimal initialBalance = command.initialBalance() == null
                ? BigDecimal.ZERO
                : command.initialBalance();

        createInitialCredit(command.userId(), initialBalance);
    }

    @Override
    public void grantReferralSignupRewards(GrantReferralSignupRewardCommand command) {
        createInitialCredit(command.newUserId(), NEW_USER_REFERRAL_REWARD);
        addCredit(command.referrerUserId(), REFERRER_REWARD);
    }

    @Override
    public CreditBalanceResult chargeCredit(ChargeCreditCommand command) {
        Credit credit = creditRepository.findByUserId(command.userId())
                .orElseGet(() -> Credit.create(command.userId(), BigDecimal.ZERO));

        credit.add(command.amount());

        return new CreditBalanceResult(
                creditRepository.save(credit).getBalance()
        );
    }

    @Override
    public CreditBalanceResult useCredit(UseCreditCommand command) {
        Credit credit = creditRepository.findByUserId(command.userId())
                .orElseGet(() -> Credit.create(command.userId(), BigDecimal.ZERO));

        credit.use(command.amount());

        return new CreditBalanceResult(
                creditRepository.save(credit).getBalance()
        );
    }

    private void createInitialCredit(Long userId, BigDecimal initialBalance) {
        if (creditRepository.findByUserId(userId).isPresent()) {
            return;
        }

        try {
            creditRepository.save(Credit.create(userId, initialBalance));
        } catch (DataIntegrityViolationException e) {
            // 같은 사용자 크레딧이 동시에 생성된 경우 이미 생성된 것으로 본다.
        }
    }

    private void addCredit(Long userId, BigDecimal amount) {
        Credit credit = creditRepository.findByUserId(userId)
                .orElseGet(() -> Credit.create(userId, BigDecimal.ZERO));

        credit.add(amount);
        creditRepository.save(credit);
    }
}