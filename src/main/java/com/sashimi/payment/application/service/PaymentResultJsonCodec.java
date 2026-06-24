package com.sashimi.payment.application.service;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.payment.application.usecase.PaymentCommandUseCase.PaymentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class PaymentResultJsonCodec {

    private final ObjectMapper objectMapper;

    public String serialize(PaymentResult result) {
        try {
            return objectMapper.writeValueAsString(result);
        } catch (JacksonException e) {
            throw new BusinessException(
                    ErrorCode.PAYMENT_IDEMPOTENCY_RESULT_INVALID
            );
        }
    }

    public PaymentResult deserialize(String resultJson) {
        try {
            return objectMapper.readValue(
                    resultJson,
                    PaymentResult.class
            );
        } catch (JacksonException e) {
            throw new BusinessException(
                    ErrorCode.PAYMENT_IDEMPOTENCY_RESULT_INVALID
            );
        }
    }
}