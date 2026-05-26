package com.sashimi.payment.domain.repository;

import com.sashimi.payment.domain.model.Payment;

import java.util.List;

public interface PaymentRepository {

    Payment save(Payment payment);

    List<Payment> findAllByUserId(Long userId);
}