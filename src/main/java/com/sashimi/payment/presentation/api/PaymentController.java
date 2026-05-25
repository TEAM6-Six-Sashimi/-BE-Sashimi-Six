package com.sashimi.payment.presentation.api;

import com.sashimi.payment.application.command.CheckoutCartCommand;
import com.sashimi.payment.application.command.PayCourseCommand;
import com.sashimi.payment.application.usecase.PaymentCommandUseCase;
import com.sashimi.payment.application.usecase.PaymentQueryUseCase;
import com.sashimi.payment.presentation.api.response.PaymentHistoryResponse;
import com.sashimi.payment.presentation.api.response.PaymentResponse;
import com.sashimi.security.principal.CustomUserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentCommandUseCase paymentCommandUseCase;
    private final PaymentQueryUseCase paymentQueryUseCase;

    public PaymentController(PaymentCommandUseCase paymentCommandUseCase,
                             PaymentQueryUseCase paymentQueryUseCase) {
        this.paymentCommandUseCase = paymentCommandUseCase;
        this.paymentQueryUseCase = paymentQueryUseCase;
    }

    @PostMapping("/cart/checkout")
    public ResponseEntity<PaymentResponse> checkoutCart(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        PaymentCommandUseCase.PaymentResult result =
                paymentCommandUseCase.checkoutCart(new CheckoutCartCommand(principal.getId()));

        return ResponseEntity.status(HttpStatus.CREATED).body(PaymentResponse.from(result));
    }

    @PostMapping("/course/{courseId}")
    public ResponseEntity<PaymentResponse> payCourse(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long courseId
    ) {
        PaymentCommandUseCase.PaymentResult result =
                paymentCommandUseCase.payCourse(new PayCourseCommand(principal.getId(), courseId));

        return ResponseEntity.status(HttpStatus.CREATED).body(PaymentResponse.from(result));
    }

    @GetMapping("/history")
    public ResponseEntity<PaymentHistoryResponse> getPaymentHistory(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        return ResponseEntity.ok(
                PaymentHistoryResponse.from(paymentQueryUseCase.getPaymentHistory(principal.getId()))
        );
    }
}