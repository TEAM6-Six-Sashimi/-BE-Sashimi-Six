package com.sashimi.credit.scheduler;

import com.sashimi.credit.application.service.CreditChargeTransactionService;
import com.sashimi.credit.domain.model.CreditChargePayment;
import com.sashimi.credit.domain.repository.CreditChargePaymentRepository;
import java.util.List;

import com.sashimi.payment.application.logging.PaymentAuditLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreditChargeRecoveryScheduler {

    private static final int MAX_RETRY_COUNT = 3;
    private static final int RETRY_BATCH_SIZE = 20;

    private final CreditChargePaymentRepository paymentRepository;
    private final CreditChargeTransactionService transactionService;
    private final PaymentAuditLogger paymentAuditLogger;

    @Scheduled(fixedDelayString = "${credit.charge.recovery.fixed-delay-ms:60000}")
    public void retryNeedRetryCharges() {
        List<CreditChargePayment> targets =
                paymentRepository.findNeedRetryTargets(
                        MAX_RETRY_COUNT,
                        RETRY_BATCH_SIZE
                );

        if (targets.isEmpty()) {
            return;
        }

        log.info("크레딧 충전 재처리 대상 조회 - count={}", targets.size());

        for (CreditChargePayment target : targets) {
            try {
                transactionService.retryNeedRetryCharge(target.getId());
            } catch (RuntimeException e) {
                try {
                    transactionService.markRetryFailed(
                            target.getId(),
                            e.getClass().getSimpleName()
                    );
                } catch (RuntimeException markException) {
                    paymentAuditLogger.creditChargeRetryFailedMarkFailed(
                            target.getId(),
                            target.getOrderId(),
                            markException.getClass().getSimpleName(),
                            markException
                    );
                }

                log.error(
                        "크레딧 충전 재처리 스케줄러 오류 - paymentId={}, orderId={}",
                        target.getId(),
                        target.getOrderId(),
                        e
                );
            }
        }
    }
}