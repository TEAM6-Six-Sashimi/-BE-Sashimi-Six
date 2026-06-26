package com.sashimi.credit.application.service;

import com.sashimi.credit.application.command.ConfirmCreditChargeCommand;
import com.sashimi.credit.application.result.CreditChargeConfirmResult;
import com.sashimi.credit.domain.model.Credit;
import com.sashimi.credit.domain.model.CreditChargePayment;
import com.sashimi.credit.domain.repository.CreditChargePaymentRepository;
import com.sashimi.credit.domain.repository.CreditRepository;
import com.sashimi.credit.infrastructure.toss.TossPaymentConfirmResponse;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditChargeTransactionService {

    private final CreditRepository creditRepository;
    private final CreditChargePaymentRepository paymentRepository;

    @Transactional
    public CreditChargeConfirmResult complete(
            ConfirmCreditChargeCommand command,
            TossPaymentConfirmResponse response
    ) {
        CreditChargePayment payment =
                paymentRepository.findByOrderIdForUpdate(command.orderId())
                        .orElseThrow(() -> new BusinessException(
                                ErrorCode.CREDIT_CHARGE_PAYMENT_NOT_FOUND
                        ));

        payment.validateOwner(command.userId());
        payment.validateAmount(command.amount());

        if (payment.isDone()) {
            payment.validatePaymentKey(command.paymentKey());
            return createCompletedResult(payment);
        }

        Credit credit = creditRepository
                .findByUserIdForUpdate(command.userId())
                .orElseGet(() -> Credit.create(command.userId(), 0L));

        credit.add(payment.getAmount());

        Credit savedCredit = creditRepository.save(credit);

        payment.markDone(
                response.paymentKey(),
                response.method(),
                response.approvedAt() == null
                        ? null
                        : response.approvedAt().toLocalDateTime(),
                savedCredit.getBalance()
        );

        paymentRepository.save(payment);

        log.info(
                "크레딧 토스 충전 완료 - userId={}, orderId={}, paymentMethod={}, amount={}, balance={}",
                command.userId(),
                payment.getOrderId(),
                payment.getPaymentMethod(),
                payment.getAmount(),
                savedCredit.getBalance()
        );

        return new CreditChargeConfirmResult(
                savedCredit.getBalance(),
                payment.getOrderId(),
                payment.getPaymentKey(),
                payment.getAmount()
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(
            ConfirmCreditChargeCommand command,
            String failureReason
    ) {
        CreditChargePayment payment =
                paymentRepository.findByOrderIdForUpdate(command.orderId())
                        .orElseThrow(() -> new BusinessException(
                                ErrorCode.CREDIT_CHARGE_PAYMENT_NOT_FOUND
                        ));

        if (payment.isDone()) {
            return;
        }

        payment.markFailed(failureReason);
        paymentRepository.save(payment);
    }

    private CreditChargeConfirmResult createCompletedResult(
            CreditChargePayment payment
    ) {
        if (payment.getBalanceAfter() == null) {
            throw new BusinessException(
                    ErrorCode.CREDIT_CHARGE_RESULT_INCONSISTENT
            );
        }

        return new CreditChargeConfirmResult(
                payment.getBalanceAfter(),
                payment.getOrderId(),
                payment.getPaymentKey(),
                payment.getAmount()
        );
    }
}