package com.sashimi.credit.domain.repository;

import com.sashimi.credit.domain.model.CreditChargePayment;

import java.util.Optional;

public interface CreditChargePaymentRepository {

    CreditChargePayment save(CreditChargePayment payment);

    Optional<CreditChargePayment> findByOrderId(String orderId);

    Optional<CreditChargePayment> findByOrderIdForUpdate(String orderId);
}