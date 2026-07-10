package com.sashimi.credit.application.service;

import com.sashimi.credit.application.command.CreateInitialCreditCommand;
import com.sashimi.credit.application.command.GrantReferralSignupRewardCommand;
import com.sashimi.credit.application.command.UseCreditCommand;
import com.sashimi.credit.application.policy.CreditChargePolicy;
import com.sashimi.credit.application.result.CreditBalanceResult;
import com.sashimi.credit.application.usecase.CreditCommandUseCase;
import com.sashimi.credit.domain.model.Credit;
import com.sashimi.credit.domain.repository.CreditRepository;
import com.sashimi.credit.application.command.ConfirmCreditChargeCommand;
import com.sashimi.credit.application.command.ReadyCreditChargeCommand;
import com.sashimi.credit.application.result.CreditChargeConfirmResult;
import com.sashimi.credit.application.result.CreditChargeReadyResult;
import com.sashimi.credit.domain.model.CreditChargePayment;
import com.sashimi.credit.domain.repository.CreditChargePaymentRepository;
import com.sashimi.credit.infrastructure.toss.TossPaymentClient;
import com.sashimi.credit.infrastructure.toss.TossPaymentConfirmResponse;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.payment.metric.PaymentMetrics;
import io.micrometer.core.instrument.Timer;

import java.util.Objects;
import org.springframework.transaction.annotation.Propagation;
import java.util.UUID;
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
    private final CreditChargePaymentRepository creditChargePaymentRepository;
    private final TossPaymentClient tossPaymentClient;
    private final CreditChargeTransactionService creditChargeTransactionService;
    private final PaymentMetrics paymentMetrics;

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

    @Override
    public CreditChargeReadyResult readyCreditCharge(ReadyCreditChargeCommand command) {
        creditChargePolicy.validate(command.amount());

        String orderId = generateCreditChargeOrderId();
        String orderName = command.amount() + " 크레딧 충전";

        CreditChargePayment payment = CreditChargePayment.ready(
                command.userId(),
                orderId,
                command.amount()
        );

        creditChargePaymentRepository.save(payment);

        log.info("크레딧 충전 결제 요청 생성 - userId={}, orderId={}, amount={}",
                command.userId(), orderId, command.amount());

        return new CreditChargeReadyResult(orderId, orderName, command.amount());
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public CreditChargeConfirmResult confirmCreditCharge(ConfirmCreditChargeCommand command) {
        Timer.Sample sample = paymentMetrics.startTimer();

        try {
            CreditChargePayment payment =
                    creditChargePaymentRepository.findByOrderId(command.orderId())
                            .orElseThrow(() -> new BusinessException(
                                    ErrorCode.CREDIT_CHARGE_PAYMENT_NOT_FOUND
                            ));

            payment.validateOwner(command.userId());
            payment.validateAmount(command.amount());

            if (payment.isDone()) {
                payment.validatePaymentKey(command.paymentKey());

                if (payment.getBalanceAfter() == null) {
                    paymentMetrics.recordTossCreditChargeInconsistency(
                            "DONE_WITHOUT_BALANCE_AFTER"
                    );

                    throw new BusinessException(
                            ErrorCode.CREDIT_CHARGE_RESULT_INCONSISTENT
                    );
                }

                paymentMetrics.recordCreditChargeProcessingSuccess(sample);

                return new CreditChargeConfirmResult(
                        payment.getBalanceAfter(),
                        payment.getOrderId(),
                        payment.getPaymentKey(),
                        payment.getAmount()
                );
            }

            if (payment.isFailed()) {
                throw new BusinessException(
                        ErrorCode.CREDIT_CHARGE_PAYMENT_ALREADY_PROCESSED
                );
            }

            if (payment.isNeedRetry() || payment.isManualReview()) {
                throw new BusinessException(
                        ErrorCode.CREDIT_CHARGE_RESULT_INCONSISTENT
                );
            }

            TossPaymentConfirmResponse response =
                    tossPaymentClient.confirmOrRetrieve(
                            command.paymentKey(),
                            command.orderId(),
                            command.amount()
                    );

            validateTossResponse(command, response);

            try {
                CreditChargeConfirmResult result =
                        creditChargeTransactionService.complete(
                                command,
                                response
                        );

                paymentMetrics.recordCreditChargeProcessingSuccess(sample);

                return result;

            } catch (RuntimeException e) {
    paymentMetrics.recordTossCreditChargeInconsistency(
            "INTERNAL_CREDIT_REFLECTION_FAILED"
    );

    try {
        creditChargeTransactionService.markNeedRetry(
                command,
                response,
                e.getClass().getSimpleName()
        );
    } catch (RuntimeException markException) {
        log.error(
                "크레딧 충전 재처리 대기 등록 실패 - userId={}, orderId={}, amount={}, reason={}",
                command.userId(),
                command.orderId(),
                command.amount(),
                markException.getClass().getSimpleName(),
                markException
        );
    }

    throw new BusinessException(
            ErrorCode.CREDIT_CHARGE_RESULT_INCONSISTENT
    );
}

        } catch (RuntimeException e) {
            paymentMetrics.recordCreditChargeProcessingFailure(sample);

            throw e;
        }
    }

    private void validateTossResponse(ConfirmCreditChargeCommand command, TossPaymentConfirmResponse response) {
        if (response == null || !"DONE".equals(response.status())) {
            creditChargeTransactionService.markFailed(command, "Toss payment status is not DONE");

            throw new BusinessException(ErrorCode.CREDIT_EXTERNAL_PAYMENT_FAILED);
        }

        if (!Objects.equals(command.orderId(), response.orderId())
                || !Objects.equals(
                command.paymentKey(),
                response.paymentKey()
        )) {
            creditChargeTransactionService.markFailed(command, "Toss payment identifier mismatch");

            throw new BusinessException(ErrorCode.CREDIT_EXTERNAL_PAYMENT_RESPONSE_MISMATCH);
        }

        if (!Objects.equals(command.amount(), response.totalAmount())) {
            creditChargeTransactionService.markFailed(command, "Toss payment amount mismatch");

            throw new BusinessException(ErrorCode.CREDIT_CHARGE_PAYMENT_AMOUNT_MISMATCH);
        }
    }

    private String generateCreditChargeOrderId() {
        return "credit_" + UUID.randomUUID().toString().replace("-", "");
    }
}
