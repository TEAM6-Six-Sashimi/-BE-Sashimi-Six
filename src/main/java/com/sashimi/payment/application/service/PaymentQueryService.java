package com.sashimi.payment.application.service;

import com.sashimi.payment.application.usecase.PaymentQueryUseCase;
import com.sashimi.payment.domain.model.Order;
import com.sashimi.payment.domain.model.Payment;
import com.sashimi.payment.domain.repository.OrderRepository;
import com.sashimi.payment.domain.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PaymentQueryService implements PaymentQueryUseCase {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentQueryService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public PaymentHistory getPaymentHistory(Long userId) {
        List<PaymentHistoryItem> items = paymentRepository.findAllByUserId(userId)
                .stream()
                .map(this::toHistoryItem)
                .toList();

        return new PaymentHistory(items);
    }

    private PaymentHistoryItem toHistoryItem(Payment payment) {
        Order order = orderRepository.findById(payment.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        return new PaymentHistoryItem(
                payment.getId(),
                order.getId(),
                order.getOrderNo(),
                payment.getAmount(),
                payment.getStatus().name(),
                order.getStatus().name(),
                payment.getPaidAt(),
                payment.getCreatedAt()
        );
    }
}