package com.sashimi.payment.metric;

import com.sashimi.payment.application.command.PaymentPurchaseType;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class PaymentMetrics {

    private static final String PURCHASE_TYPE = "purchase_type";
    private static final String RESULT = "result";
    private static final String REASON = "reason";

    private static final String SUCCESS = "SUCCESS";
    private static final String FAILURE = "FAILURE";
    private static final String UNKNOWN = "UNKNOWN";

    private final MeterRegistry meterRegistry;

    public PaymentMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    public void recordPaymentProcessingSuccess(
            Timer.Sample sample,
            PaymentPurchaseType purchaseType
    ) {
        recordPaymentProcessing(
                sample,
                purchaseTypeLabel(purchaseType),
                SUCCESS
        );
    }

    public void recordPaymentProcessingFailure(
            Timer.Sample sample,
            PaymentPurchaseType purchaseType
    ) {
        recordPaymentProcessing(
                sample,
                purchaseTypeLabel(purchaseType),
                FAILURE
        );
    }

    public void recordCreditChargeProcessingSuccess(
            Timer.Sample sample
    ) {
        recordPaymentProcessing(
                sample,
                "CREDIT_CHARGE",
                SUCCESS
        );
    }

    public void recordCreditChargeProcessingFailure(
            Timer.Sample sample
    ) {
        recordPaymentProcessing(
                sample,
                "CREDIT_CHARGE",
                FAILURE
        );
    }

    private void recordPaymentProcessing(
            Timer.Sample sample,
            String purchaseType,
            String result
    ) {
        sample.stop(
                Timer.builder("payment.processing.duration")
                        .description("Time spent processing payment requests")
                        .tag(PURCHASE_TYPE, purchaseType)
                        .tag(RESULT, result)
                        .publishPercentileHistogram()
                        .register(meterRegistry)
        );
    }

    public void recordPaymentPreview(
            PaymentPurchaseType purchaseType
    ) {
        Counter.builder("payment.preview.requests")
                .description("Number of payment preview requests")
                .tag(PURCHASE_TYPE, purchaseTypeLabel(purchaseType))
                .register(meterRegistry)
                .increment();
    }

    public void recordPaymentCompleted(
            PaymentPurchaseType purchaseType
    ) {
        Counter.builder("payment.completed")
                .description("Number of completed payments")
                .tag(PURCHASE_TYPE, purchaseTypeLabel(purchaseType))
                .register(meterRegistry)
                .increment();
    }

    public void recordPaymentIdempotencyReused(
            PaymentPurchaseType purchaseType
    ) {
        Counter.builder("payment.idempotency.reused")
                .description("Number of duplicated payment requests safely handled by idempotency")
                .tag(PURCHASE_TYPE, purchaseTypeLabel(purchaseType))
                .register(meterRegistry)
                .increment();
    }

    public void recordPaymentIdempotencyConflict(
            PaymentPurchaseType purchaseType
    ) {
        Counter.builder("payment.idempotency.conflict")
                .description("Number of payment requests blocked because the same idempotency key was used with different request data")
                .tag(PURCHASE_TYPE, purchaseTypeLabel(purchaseType))
                .register(meterRegistry)
                .increment();
    }

    public void recordPaymentDuplicateCompleted(
            PaymentPurchaseType purchaseType
    ) {
        Counter.builder("payment.duplicate.completed")
                .description("Number of detected duplicate payment completion risks")
                .tag(PURCHASE_TYPE, purchaseTypeLabel(purchaseType))
                .register(meterRegistry)
                .increment();
    }

    public void recordTossCreditChargeInconsistency(
            String reason
    ) {
        Counter.builder("toss.credit.charge.inconsistency")
                .description("Number of inconsistencies between Toss approval and internal credit charge result")
                .tag(REASON, reason == null || reason.isBlank()
                        ? UNKNOWN
                        : reason)
                .register(meterRegistry)
                .increment();
    }

    private String purchaseTypeLabel(
            PaymentPurchaseType purchaseType
    ) {
        return purchaseType == null
                ? UNKNOWN
                : purchaseType.name();
    }
}