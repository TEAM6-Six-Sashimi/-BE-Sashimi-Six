package com.sashimi.credit.application.service;

import com.sashimi.credit.application.result.CreditBalanceResult;
import com.sashimi.credit.application.usecase.CreditQueryUseCase;
import com.sashimi.credit.domain.model.Credit;
import com.sashimi.credit.domain.repository.CreditChargePaymentRepository;
import com.sashimi.credit.domain.repository.CreditRepository;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreditQueryService implements CreditQueryUseCase {

    private final CreditRepository creditRepository;
    private final CreditChargePaymentRepository creditChargePaymentRepository;

    @Override
    public CreditBalanceResult getBalance(Long userId) {
        Long balance = creditRepository.findByUserId(userId)
                .map(Credit::getBalance)
                .orElse(0L);

        return new CreditBalanceResult(balance);
    }

    @Override
    public CreditChargeHistory getChargeHistory(
            Long userId,
            int page,
            int size
    ) {
        if (page < 0 || size < 1 || size > 100) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        CreditChargePaymentRepository.PageResult result =
                creditChargePaymentRepository
                        .findCompletedByUserId(
                                userId,
                                page,
                                size
                        );

        List<CreditChargeHistoryItem> items =
                result.content()
                        .stream()
                        .map(payment ->
                                new CreditChargeHistoryItem(
                                        payment.getId(),
                                        payment.getOrderId(),
                                        payment.getAmount(),
                                        payment.getAmount(),
                                        payment.getApprovedAt(),
                                        payment.getPaymentMethod()
                                )
                        )
                        .toList();

        return new CreditChargeHistory(
                items,
                result.totalElements(),
                result.totalPages(),
                result.page(),
                result.size()
        );
    }
}