package com.sashimi.payment.application.service;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.payment.application.command.PaymentCheckoutCommand;
import com.sashimi.payment.application.command.PaymentPurchaseType;
import com.sashimi.payment.application.usecase.PaymentCommandUseCase.PaidCourse;
import com.sashimi.payment.application.usecase.PaymentCommandUseCase.PaymentResult;
import com.sashimi.payment.domain.model.PaymentIdempotency;
import com.sashimi.subscription.domain.model.SubscriptionPlan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.List;

import static com.sashimi.payment.application.service.PaymentIdempotencyTransactionService.ExistingRequestResolution;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentCommandServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long IDEMPOTENCY_ID = 100L;
    private static final String IDEMPOTENCY_KEY = "checkout-key-001";

    private PaymentIdempotencyTransactionService
            idempotencyTransactionService;

    private PaymentCheckoutTransactionService
            checkoutTransactionService;

    private PaymentResultJsonCodec paymentResultJsonCodec;

    private PaymentCommandService paymentCommandService;

    @BeforeEach
    void setUp() {
        idempotencyTransactionService =
                mock(PaymentIdempotencyTransactionService.class);

        checkoutTransactionService =
                mock(PaymentCheckoutTransactionService.class);

        paymentResultJsonCodec =
                mock(PaymentResultJsonCodec.class);

        paymentCommandService = new PaymentCommandService(
                idempotencyTransactionService,
                checkoutTransactionService,
                paymentResultJsonCodec
        );
    }

    @Test
    void checkout_creates_processing_idempotency_and_executes_payment() {
        PaymentCheckoutCommand command = courseCommand();
        PaymentResult expectedResult = courseResult();

        when(idempotencyTransactionService.createProcessing(
                USER_ID,
                IDEMPOTENCY_KEY,
                "COURSE:10:-:true"
        )).thenReturn(processingIdempotency());

        when(checkoutTransactionService.execute(
                command,
                IDEMPOTENCY_ID
        )).thenReturn(expectedResult);

        PaymentResult result =
                paymentCommandService.checkout(command);

        assertThat(result).isEqualTo(expectedResult);

        verify(idempotencyTransactionService)
                .createProcessing(
                        USER_ID,
                        IDEMPOTENCY_KEY,
                        "COURSE:10:-:true"
                );

        verify(checkoutTransactionService)
                .execute(command, IDEMPOTENCY_ID);

        verify(idempotencyTransactionService, never())
                .markFailed(anyLong());
    }

    @Test
    void checkout_returns_saved_result_when_same_request_already_completed() {
        PaymentCheckoutCommand command = courseCommand();
        PaymentResult expectedResult = courseResult();
        String savedResultJson = "{\"status\":\"PAID\"}";

        when(idempotencyTransactionService.createProcessing(
                USER_ID,
                IDEMPOTENCY_KEY,
                "COURSE:10:-:true"
        )).thenThrow(new DataIntegrityViolationException(
                "duplicate idempotency key"
        ));

        when(idempotencyTransactionService.resolveExisting(
                USER_ID,
                IDEMPOTENCY_KEY,
                "COURSE:10:-:true"
        )).thenReturn(
                ExistingRequestResolution.completed(savedResultJson)
        );

        when(paymentResultJsonCodec.deserialize(savedResultJson))
                .thenReturn(expectedResult);

        PaymentResult result =
                paymentCommandService.checkout(command);

        assertThat(result).isEqualTo(expectedResult);

        verify(checkoutTransactionService, never())
                .execute(
                        org.mockito.ArgumentMatchers.any(),
                        anyLong()
                );
    }

    @Test
    void checkout_rejects_when_same_request_is_processing() {
        PaymentCheckoutCommand command = courseCommand();

        when(idempotencyTransactionService.createProcessing(
                USER_ID,
                IDEMPOTENCY_KEY,
                "COURSE:10:-:true"
        )).thenThrow(new DataIntegrityViolationException(
                "duplicate idempotency key"
        ));

        when(idempotencyTransactionService.resolveExisting(
                USER_ID,
                IDEMPOTENCY_KEY,
                "COURSE:10:-:true"
        )).thenReturn(
                ExistingRequestResolution.processing()
        );

        BusinessException exception =
                catchThrowableOfType(
                        () -> paymentCommandService.checkout(command),
                        BusinessException.class
                );

        assertThat(exception.getErrorCode())
                .isEqualTo(
                        ErrorCode.PAYMENT_IDEMPOTENCY_PROCESSING
                );

        verify(checkoutTransactionService, never())
                .execute(
                        org.mockito.ArgumentMatchers.any(),
                        anyLong()
                );
    }

    @Test
    void checkout_rejects_when_same_request_failed_before() {
        PaymentCheckoutCommand command = courseCommand();

        when(idempotencyTransactionService.createProcessing(
                USER_ID,
                IDEMPOTENCY_KEY,
                "COURSE:10:-:true"
        )).thenThrow(new DataIntegrityViolationException(
                "duplicate idempotency key"
        ));

        when(idempotencyTransactionService.resolveExisting(
                USER_ID,
                IDEMPOTENCY_KEY,
                "COURSE:10:-:true"
        )).thenReturn(
                ExistingRequestResolution.failed()
        );

        BusinessException exception =
                catchThrowableOfType(
                        () -> paymentCommandService.checkout(command),
                        BusinessException.class
                );

        assertThat(exception.getErrorCode())
                .isEqualTo(
                        ErrorCode.PAYMENT_IDEMPOTENCY_FAILED
                );

        verify(checkoutTransactionService, never())
                .execute(
                        org.mockito.ArgumentMatchers.any(),
                        anyLong()
                );
    }

    @Test
    void checkout_marks_idempotency_failed_when_payment_execution_fails() {
        PaymentCheckoutCommand command = courseCommand();

        when(idempotencyTransactionService.createProcessing(
                USER_ID,
                IDEMPOTENCY_KEY,
                "COURSE:10:-:true"
        )).thenReturn(processingIdempotency());

        when(checkoutTransactionService.execute(
                command,
                IDEMPOTENCY_ID
        )).thenThrow(
                new BusinessException(
                        ErrorCode.CREDIT_INSUFFICIENT_BALANCE
                )
        );

        BusinessException exception =
                catchThrowableOfType(
                        () -> paymentCommandService.checkout(command),
                        BusinessException.class
                );

        assertThat(exception.getErrorCode())
                .isEqualTo(
                        ErrorCode.CREDIT_INSUFFICIENT_BALANCE
                );

        verify(idempotencyTransactionService)
                .markFailed(IDEMPOTENCY_ID);
    }

    @Test
    void checkout_rejects_blank_idempotency_key() {
        PaymentCheckoutCommand command =
                new PaymentCheckoutCommand(
                        USER_ID,
                        PaymentPurchaseType.COURSE,
                        10L,
                        null,
                        true,
                        " "
                );

        BusinessException exception =
                catchThrowableOfType(
                        () -> paymentCommandService.checkout(command),
                        BusinessException.class
                );

        assertThat(exception.getErrorCode())
                .isEqualTo(
                        ErrorCode.PAYMENT_IDEMPOTENCY_KEY_INVALID
                );

        verify(idempotencyTransactionService, never())
                .createProcessing(
                        anyLong(),
                        anyString(),
                        anyString()
                );
    }

    @Test
    void checkout_rejects_too_long_idempotency_key() {
        String tooLongKey = "a".repeat(101);

        PaymentCheckoutCommand command =
                new PaymentCheckoutCommand(
                        USER_ID,
                        PaymentPurchaseType.COURSE,
                        10L,
                        null,
                        true,
                        tooLongKey
                );

        BusinessException exception =
                catchThrowableOfType(
                        () -> paymentCommandService.checkout(command),
                        BusinessException.class
                );

        assertThat(exception.getErrorCode())
                .isEqualTo(
                        ErrorCode.PAYMENT_IDEMPOTENCY_KEY_INVALID
                );

        verify(idempotencyTransactionService, never())
                .createProcessing(
                        anyLong(),
                        anyString(),
                        anyString()
                );
    }

    @Test
    void checkout_fingerprint_contains_agreement_value() {
        PaymentCheckoutCommand command =
                new PaymentCheckoutCommand(
                        USER_ID,
                        PaymentPurchaseType.AI_SUBSCRIPTION,
                        null,
                        SubscriptionPlan.MONTHLY,
                        true,
                        IDEMPOTENCY_KEY
                );

        PaymentResult expectedResult =
                new PaymentResult(
                        2L,
                        "ORD-SUB-001",
                        3L,
                        PaymentPurchaseType.AI_SUBSCRIPTION,
                        10_000L,
                        "PAID",
                        90_000L,
                        List.of(),
                        null
                );

        when(idempotencyTransactionService.createProcessing(
                USER_ID,
                IDEMPOTENCY_KEY,
                "AI_SUBSCRIPTION:-:MONTHLY:true"
        )).thenReturn(processingIdempotency());

        when(checkoutTransactionService.execute(
                command,
                IDEMPOTENCY_ID
        )).thenReturn(expectedResult);

        PaymentResult result =
                paymentCommandService.checkout(command);

        assertThat(result).isEqualTo(expectedResult);

        verify(idempotencyTransactionService)
                .createProcessing(
                        USER_ID,
                        IDEMPOTENCY_KEY,
                        "AI_SUBSCRIPTION:-:MONTHLY:true"
                );
    }

    private PaymentCheckoutCommand courseCommand() {
        return new PaymentCheckoutCommand(
                USER_ID,
                PaymentPurchaseType.COURSE,
                10L,
                null,
                true,
                IDEMPOTENCY_KEY
        );
    }

    private PaymentResult courseResult() {
        return new PaymentResult(
                1L,
                "ORD-TEST-001",
                10L,
                PaymentPurchaseType.COURSE,
                30_000L,
                "PAID",
                70_000L,
                List.of(
                        new PaidCourse(
                                10L,
                                "Spring Boot Basic",
                                30_000L
                        )
                ),
                null
        );
    }

    private PaymentIdempotency processingIdempotency() {
        LocalDateTime now = LocalDateTime.now();

        return PaymentIdempotency.restore(
                IDEMPOTENCY_ID,
                USER_ID,
                IDEMPOTENCY_KEY,
                "COURSE:10:-:true",
                com.sashimi.payment.domain.model
                        .PaymentIdempotencyStatus.PROCESSING,
                null,
                now,
                now
        );
    }
}