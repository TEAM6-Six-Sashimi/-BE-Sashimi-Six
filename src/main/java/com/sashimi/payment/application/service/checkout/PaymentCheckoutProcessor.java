package com.sashimi.payment.application.service.checkout;

import com.sashimi.payment.application.command.PaymentCheckoutCommand;
import com.sashimi.payment.application.command.PaymentPurchaseType;
import com.sashimi.payment.application.usecase.PaymentCommandUseCase.PaymentResult;

public interface PaymentCheckoutProcessor {

    PaymentPurchaseType supports();

    PaymentResult checkout(PaymentCheckoutCommand command);
}