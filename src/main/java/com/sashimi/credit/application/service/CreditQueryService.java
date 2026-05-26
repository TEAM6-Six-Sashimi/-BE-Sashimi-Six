package com.sashimi.credit.application.service;

import com.sashimi.credit.application.result.CreditBalanceResult;
import com.sashimi.credit.application.usecase.CreditQueryUseCase;
import com.sashimi.credit.domain.model.Credit;
import com.sashimi.credit.domain.repository.CreditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreditQueryService implements CreditQueryUseCase {

    private final CreditRepository creditRepository;

    @Override
    public CreditBalanceResult getBalance(Long userId) {
        BigDecimal balance = creditRepository.findByUserId(userId)
                .map(Credit::getBalance)
                .orElse(BigDecimal.ZERO);

        return new CreditBalanceResult(balance);
    }
}