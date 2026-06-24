package com.sashimi.credit.infrastructure.toss;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
public class TossPaymentClient {

    private final RestClient restClient;

    public TossPaymentClient(
            @Qualifier("tossPaymentRestClient") RestClient restClient
    ) {
        this.restClient = restClient;
    }

    public TossPaymentConfirmResponse confirmOrRetrieve(
            String paymentKey,
            String orderId,
            Long amount
    ) {
        try {
            return confirm(paymentKey, orderId, amount);
        } catch (RestClientException confirmException) {
            log.warn(
                    "토스 결제 승인 응답 확인 실패, 결제 조회로 복구 시도 - orderId={}",
                    orderId
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
            log.error(
                    "토스 결제 승인 및 조회 모두 실패 - orderId={}",
                    orderId
            );

            throw new BusinessException(
                    ErrorCode.CREDIT_EXTERNAL_PAYMENT_FAILED
            );
        }
    }
}