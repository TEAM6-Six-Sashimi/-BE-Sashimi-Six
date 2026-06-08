package com.sashimi.credit.application.service;

import com.sashimi.credit.application.result.CreditBalanceResult;
import com.sashimi.credit.application.usecase.CreditQueryUseCase;
import com.sashimi.credit.domain.model.Credit;
import com.sashimi.credit.domain.repository.CreditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreditQueryService implements CreditQueryUseCase {

    private final CreditRepository creditRepository;

    @Override
    public CreditBalanceResult getBalance(Long userId) {
        Long balance = creditRepository.findByUserId(userId)
                .map(Credit::getBalance)
                .orElse(0L);

        return new CreditBalanceResult(balance);
    }
}