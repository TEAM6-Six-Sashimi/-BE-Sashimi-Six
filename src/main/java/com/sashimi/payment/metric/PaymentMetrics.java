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
                purchaseType.name(),
                SUCCESS
        );
    }

    public void recordPaymentProcessingFailure(
            Timer.Sample sample,
            PaymentPurchaseType purchaseType
    ) {
        String purchaseTypeLabel = purchaseType == null
                ? "UNKNOWN"
                : purchaseType.name();

        recordPaymentProcessing(
                sample,
                purchaseTypeLabel,
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
                .tag(PURCHASE_TYPE, purchaseType.name())
                .register(meterRegistry)
                .increment();
    }

    public void recordPaymentCompleted(
            PaymentPurchaseType purchaseType
    ) {
        Counter.builder("payment.completed")
                .description("Number of completed payments")
                .tag(PURCHASE_TYPE, purchaseType.name())
                .register(meterRegistry)
                .increment();
    }

    public void recordTossCreditChargeInconsistency(
            String reason
    ) {
        Counter.builder("toss.credit.charge.inconsistency")
                .description("Number of inconsistencies between Toss approval and internal credit charge result")
                .tag(REASON, reason)
                .register(meterRegistry)
                .increment();
    }
}