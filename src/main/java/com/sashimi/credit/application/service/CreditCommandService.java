package com.sashimi.credit.application.service;

import com.sashimi.credit.application.command.ChargeCreditCommand;
import com.sashimi.credit.application.command.CreateInitialCreditCommand;
import com.sashimi.credit.application.command.GrantReferralSignupRewardCommand;
import com.sashimi.credit.application.command.UseCreditCommand;
import com.sashimi.credit.application.policy.CreditChargePolicy;
import com.sashimi.credit.application.result.CreditBalanceResult;
import com.sashimi.credit.application.usecase.CreditCommandUseCase;
import com.sashimi.credit.domain.model.Credit;
import com.sashimi.credit.domain.repository.CreditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CreditCommandService implements CreditCommandUseCase {

    public static final Long NEW_USER_REFERRAL_REWARD = 1000L;
    public static final Long REFERRER_REWARD = 1000L;

    private final CreditRepository creditRepository;
    private final CreditChargePolicy creditChargePolicy;

    @Override
    public void createInitialCredit(CreateInitialCreditCommand command) {
        Long initialBalance = command.initialBalance() == null
                ? 0L
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
        creditChargePolicy.validate(command.amount());

        Credit credit = getOrCreateCreditForUpdate(command.userId());

        credit.add(command.amount());

        return new CreditBalanceResult(
                creditRepository.save(credit).getBalance()
        );
    }

    @Override
    public CreditBalanceResult useCredit(UseCreditCommand command) {
        Credit credit = getOrCreateCreditForUpdate(command.userId());

        credit.use(command.amount());

        Credit savedCredit = creditRepository.save(credit);

        log.info("크레딧 사용 완료 - userId={}, usedAmount={}, balance={}",
                command.userId(), command.amount(), savedCredit.getBalance());

        return new CreditBalanceResult(savedCredit.getBalance());
    }

    private void createInitialCredit(Long userId, Long initialBalance) {
        if (creditRepository.findByUserId(userId).isPresent()) {
            return;
        }

        try {
            creditRepository.save(Credit.create(userId, initialBalance));
        } catch (DataIntegrityViolationException e) {

        }
    }

    private void addCredit(Long userId, Long amount) {
        Credit credit = getOrCreateCreditForUpdate(userId);

        credit.add(amount);
        creditRepository.save(credit);
    }

    private Credit getOrCreateCreditForUpdate(Long userId) {
        return creditRepository.findByUserIdForUpdate(userId)
                .orElseGet(() -> Credit.create(userId, 0L));
    }
}