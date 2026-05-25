package com.sashimi.credit.application.service;

import com.sashimi.credit.domain.model.Credit;
import com.sashimi.credit.domain.repository.CreditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class CreditService {

    public static final BigDecimal NEW_USER_REFERRAL_REWARD = BigDecimal.valueOf(5000);
    public static final BigDecimal REFERRER_REWARD = BigDecimal.valueOf(1000);

    private final CreditRepository creditRepository;

    public void createInitialCredit(Long userId) {
        createInitialCredit(userId, BigDecimal.ZERO);
    }

    public void grantReferralSignupRewards(Long newUserId, Long referrerUserId) {
        createInitialCredit(newUserId, NEW_USER_REFERRAL_REWARD);
        addCredit(referrerUserId, REFERRER_REWARD);
    }

    private void createInitialCredit(Long userId, BigDecimal initialBalance) {
        if (creditRepository.findByUserId(userId).isPresent()) {
            return;
        }

        creditRepository.save(Credit.create(userId, initialBalance));
    }

    private void addCredit(Long userId, BigDecimal amount) {
        Credit credit = creditRepository.findByUserId(userId)
                .orElseGet(() -> Credit.create(userId, BigDecimal.ZERO));

        credit.add(amount);
        creditRepository.save(credit);
    }
}
