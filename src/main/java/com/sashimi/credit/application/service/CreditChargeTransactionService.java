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

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditChargeTransactionService {

    private final CreditRepository creditRepository;
    private final CreditChargePaymentRepository paymentRepository;
    private static final int MAX_CREDIT_CHARGE_RETRY_COUNT = 3;

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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markNeedRetry(
            ConfirmCreditChargeCommand command,
            TossPaymentConfirmResponse response,
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

        payment.validateOwner(command.userId());
        payment.validateAmount(command.amount());

        payment.markNeedRetry(
                response.paymentKey(),
                response.method(),
                response.approvedAt() == null
                        ? LocalDateTime.now()
                        : response.approvedAt().toLocalDateTime(),
                failureReason
        );

        paymentRepository.save(payment);

        log.error(
                "크레딧 충전 내부 반영 실패로 재처리 대기 등록 - userId={}, orderId={}, amount={}, reason={}",
                command.userId(),
                payment.getOrderId(),
                payment.getAmount(),
                failureReason
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void retryNeedRetryCharge(Long paymentId) {
        CreditChargePayment payment =
                paymentRepository.findByIdForUpdate(paymentId)
                        .orElseThrow(() -> new BusinessException(
                                ErrorCode.CREDIT_CHARGE_PAYMENT_NOT_FOUND
                        ));

        if (!payment.isNeedRetry()) {
            return;
        }

        Credit credit = creditRepository
                .findByUserIdForUpdate(payment.getUserId())
                .orElseGet(() -> Credit.create(payment.getUserId(), 0L));

        credit.add(payment.getAmount());
        Credit savedCredit = creditRepository.save(credit);

        payment.markDone(
                payment.getPaymentKey(),
                payment.getPaymentMethod(),
                payment.getApprovedAt(),
                savedCredit.getBalance()
        );

        paymentRepository.save(payment);

        log.info(
                "크레딧 충전 재처리 성공 - userId={}, orderId={}, amount={}, balance={}",
                payment.getUserId(),
                payment.getOrderId(),
                payment.getAmount(),
                savedCredit.getBalance()
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markRetryFailed(
            Long paymentId,
            String failureReason
    ) {
        CreditChargePayment payment =
                paymentRepository.findByIdForUpdate(paymentId)
                        .orElseThrow(() -> new BusinessException(
                                ErrorCode.CREDIT_CHARGE_PAYMENT_NOT_FOUND
                        ));

        if (!payment.isNeedRetry()) {
            return;
        }

        payment.markRetryFailed(
                failureReason,
                LocalDateTime.now(),
                MAX_CREDIT_CHARGE_RETRY_COUNT
        );

        paymentRepository.save(payment);

        log.error(
                "크레딧 충전 재처리 실패 - paymentId={}, orderId={}, retryCount={}, reason={}",
                payment.getId(),
                payment.getOrderId(),
                payment.getRetryCount(),
                failureReason
        );
    }
}