package com.sashimi.payment.application.logging;

import com.sashimi.payment.application.command.PaymentPurchaseType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentAuditLogger {

    public void coursePaymentCompleted(
            Long userId,
            Long orderId,
            Long paymentId,
            Long amount,
            int courseCount
    ) {
        log.info(
                "강의 결제 완료 - userId={}, orderId={}, paymentId={}, amount={}, courseCount={}",
                userId,
                orderId,
                paymentId,
                amount,
                courseCount
        );
    }

    public void cartPaymentCompleted(
            Long userId,
            Long orderId,
            Long paymentId,
            Long amount,
            int courseCount
    ) {
        log.info(
                "장바구니 결제 완료 - userId={}, orderId={}, paymentId={}, amount={}, courseCount={}",
                userId,
                orderId,
                paymentId,
                amount,
                courseCount
        );
    }

    public void subscriptionPaymentCompleted(
            Long userId,
            Long subscriptionId,
            Long orderId,
            Long paymentId,
            String plan,
            Long amount
    ) {
        log.info(
                "AI 구독권 결제 완료 - userId={}, subscriptionId={}, orderId={}, paymentId={}, plan={}, amount={}",
                userId,
                subscriptionId,
                orderId,
                paymentId,
                plan,
                amount
        );
    }

    public void creditUsed(
            Long userId,
            Long usedAmount,
            Long balance
    ) {
        log.info(
                "크레딧 사용 완료 - userId={}, usedAmount={}, balance={}",
                userId,
                usedAmount,
                balance
        );
    }

    public void creditChargeReady(
            Long userId,
            String orderId,
            Long amount
    ) {
        log.info(
                "크레딧 충전 결제 요청 생성 - userId={}, orderId={}, amount={}",
                userId,
                orderId,
                amount
        );
    }

    public void creditChargeCompleted(
            Long userId,
            String orderId,
            String paymentMethod,
            Long amount,
            Long balance
    ) {
        log.info(
                "크레딧 토스 충전 완료 - userId={}, orderId={}, paymentMethod={}, amount={}, balance={}",
                userId,
                orderId,
                paymentMethod,
                amount,
                balance
        );
    }

    public void creditChargeFailed(
            Long userId,
            String orderId,
            Long amount,
            String reason
    ) {
        log.warn(
                "크레딧 충전 결제 실패 처리 - userId={}, orderId={}, amount={}, reason={}",
                userId,
                orderId,
                amount,
                reason
        );
    }

    public void creditChargeNeedRetry(
            Long userId,
            String orderId,
            Long amount,
            String reason
    ) {
        log.error(
                "크레딧 충전 내부 반영 실패로 재처리 대기 등록 - userId={}, orderId={}, amount={}, reason={}",
                userId,
                orderId,
                amount,
                reason
        );
    }

    public void creditChargeNeedRetryMarkFailed(
            Long userId,
            String orderId,
            Long amount,
            String reason,
            RuntimeException exception
    ) {
        log.error(
                "크레딧 충전 재처리 대기 등록 실패 - userId={}, orderId={}, amount={}, reason={}",
                userId,
                orderId,
                amount,
                reason,
                exception
        );
    }

    public void creditChargeRetrySucceeded(
            Long userId,
            String orderId,
            Long amount,
            Long balance
    ) {
        log.info(
                "크레딧 충전 재처리 성공 - userId={}, orderId={}, amount={}, balance={}",
                userId,
                orderId,
                amount,
                balance
        );
    }

    public void creditChargeRetryFailed(
            Long paymentId,
            String orderId,
            int retryCount,
            String reason
    ) {
        log.error(
                "크레딧 충전 재처리 실패 - paymentId={}, orderId={}, retryCount={}, reason={}",
                paymentId,
                orderId,
                retryCount,
                reason
        );
    }

    public void completedCreditChargeResultInvalid(
            Long userId,
            String orderId,
            Long amount,
            String reason
    ) {
        log.error(
                "완료된 크레딧 충전 결과 정합성 오류 - userId={}, orderId={}, amount={}, reason={}",
                userId,
                orderId,
                amount,
                reason
        );
    }

    public void tossConfirmFallbackStarted(
            String orderId,
            String reason
    ) {
        log.warn(
                "토스 결제 승인 응답 확인 실패, 결제 조회로 복구 시도 - orderId={}, reason={}",
                orderId,
                reason
        );
    }

    public void tossConfirmAndRetrieveFailed(
            String orderId,
            String reason
    ) {
        log.error(
                "토스 결제 승인 및 조회 모두 실패 - orderId={}, reason={}",
                orderId,
                reason
        );
    }

    public void idempotencyReused(
            Long userId,
            PaymentPurchaseType purchaseType
    ) {
        log.info(
                "결제 멱등성 요청 재사용 - userId={}, purchaseType={}",
                userId,
                purchaseType
        );
    }

    public void idempotencyConflict(
            Long userId,
            PaymentPurchaseType purchaseType
    ) {
        log.warn(
                "결제 멱등성 키 충돌 - userId={}, purchaseType={}",
                userId,
                purchaseType
        );
    }

    public void creditChargeRetryFailedMarkFailed(
            Long paymentId,
            String orderId,
            String reason,
            RuntimeException exception
    ) {
        log.error(
                "크레딧 충전 재처리 실패 상태 변경 실패 - paymentId={}, orderId={}, reason={}",
                paymentId,
                orderId,
                reason,
                exception
        );
    }
}