package com.sashimi.credit.infrastructure.toss;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.payment.application.logging.PaymentAuditLogger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;


@Component
public class    TossPaymentClient {

    private final RestClient restClient;
    private final PaymentAuditLogger paymentAuditLogger;

    public TossPaymentClient(
            @Qualifier("tossPaymentRestClient") RestClient restClient, PaymentAuditLogger paymentAuditLogger) {
        this.restClient = restClient;
        this.paymentAuditLogger = paymentAuditLogger;
    }

    public TossPaymentConfirmResponse confirmOrRetrieve(
            String paymentKey,
            String orderId,
            Long amount
    ) {
        try {
            return confirm(paymentKey, orderId, amount);
        } catch (RestClientException confirmException) {
            paymentAuditLogger.tossConfirmFallbackStarted(
                    orderId,
                    confirmException.getClass().getSimpleName()
            );

            return retrieveApprovedPayment(paymentKey, orderId);
        }
    }

    private TossPaymentConfirmResponse confirm(
            String paymentKey,
            String orderId,
            Long amount
    ) {
        return restClient.post()
                .uri("/v1/payments/confirm")
                .body(new TossPaymentConfirmRequest(
                        paymentKey,
                        orderId,
                        amount
                ))
                .retrieve()
                .requiredBody(TossPaymentConfirmResponse.class);
    }

    private TossPaymentConfirmResponse retrieveApprovedPayment(
            String paymentKey,
            String orderId
    ) {
        try {
            return restClient.get()
                    .uri("/v1/payments/{paymentKey}", paymentKey)
                    .retrieve()
                    .requiredBody(TossPaymentConfirmResponse.class);
        } catch (RestClientException retrieveException) {
            paymentAuditLogger.tossConfirmAndRetrieveFailed(
                    orderId,
                    retrieveException.getClass().getSimpleName()
            );

            throw new BusinessException(
                    ErrorCode.CREDIT_EXTERNAL_PAYMENT_FAILED
            );
        }
    }
}