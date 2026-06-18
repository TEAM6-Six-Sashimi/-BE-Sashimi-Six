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
    public CreditChargeConfirmResult confirmCreditCharge(ConfirmCreditChargeCommand command) {
        CreditChargePayment payment = creditChargePaymentRepository.findByOrderIdForUpdate(command.orderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CREDIT_CHARGE_PAYMENT_NOT_FOUND));

        payment.validateOwner(command.userId());
        payment.validateAmount(command.amount());

        if (payment.isDone()) {
            Credit credit = getOrCreateCreditForUpdate(command.userId());

            return new CreditChargeConfirmResult(
                    credit.getBalance(),
                    payment.getOrderId(),
                    payment.getPaymentKey(),
                    payment.getAmount()
            );
        }

        TossPaymentConfirmResponse tossResponse;

        try {
            tossResponse = tossPaymentClient.confirm(
                    command.paymentKey(),
                    command.orderId(),
                    command.amount()
            );
        } catch (BusinessException e) {
            throw e;
        }

        if (tossResponse == null || !"DONE".equals(tossResponse.status())) {
            payment.markFailed("Toss payment approval failed");
            creditChargePaymentRepository.save(payment);
            throw new BusinessException(ErrorCode.CREDIT_EXTERNAL_PAYMENT_FAILED);
        }

        if (!command.amount().equals(tossResponse.totalAmount())) {
            payment.markFailed("Toss payment amount mismatch");
            creditChargePaymentRepository.save(payment);
            throw new BusinessException(ErrorCode.CREDIT_CHARGE_PAYMENT_AMOUNT_MISMATCH);
        }

        payment.markDone(
                tossResponse.paymentKey(),
                tossResponse.method(),
                tossResponse.approvedAt() == null
                        ? null
                        : tossResponse.approvedAt().toLocalDateTime()
        );
        creditChargePaymentRepository.save(payment);

        Credit credit = getOrCreateCreditForUpdate(command.userId());
        credit.add(payment.getAmount());

        Credit savedCredit = creditRepository.save(credit);

        log.info(
                "크레딧 토스 충전 완료 - userId={}, orderId={}, paymentKey={}, paymentMethod={}, amount={}, balance={}",
                command.userId(),
                payment.getOrderId(),
                payment.getPaymentKey(),
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

    private String generateCreditChargeOrderId() {
        return "credit_" + UUID.randomUUID().toString().replace("-", "");
    }
}