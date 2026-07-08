package com.sashimi.credit.infrastructure.persistence;

import com.sashimi.credit.domain.model.CreditChargePaymentStatus;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AdminCreditChargeQueryRepository {

    private final EntityManager entityManager;

    public PageResult<AdminCreditChargePaymentRow> findCreditChargePayments(
            LocalDateTime startAt,
            LocalDateTime endAt,
            String keyword,
            int page,
            int size
    ) {
        String where = """
                where p.status = :status
                  and (:startAt is null or p.approvedAt >= :startAt)
                  and (:endAt is null or p.approvedAt < :endAt)
                  and (
                      :keyword is null
                      or lower(u.loginId) like concat('%', :keyword, '%')
                      or lower(p.orderId) like concat('%', :keyword, '%')
                  )
                """;

        Long total = entityManager.createQuery(
                        "select count(p.id) " +
                                "from CreditChargePaymentJpaEntity p " +
                                "join UserJpaEntity u on u.id = p.userId " +
                                where,
                        Long.class
                )
                .setParameter("status", CreditChargePaymentStatus.DONE)
                .setParameter("startAt", startAt)
                .setParameter("endAt", endAt)
                .setParameter("keyword", keyword)
                .getSingleResult();

        List<AdminCreditChargePaymentRow> items = entityManager.createQuery(
                        "select new com.sashimi.credit.infrastructure.persistence.AdminCreditChargeQueryRepository$AdminCreditChargePaymentRow(" +
                                "p.id, p.orderId, u.loginId, p.amount, p.paymentMethod, p.amount, p.approvedAt" +
                                ") " +
                                "from CreditChargePaymentJpaEntity p " +
                                "join UserJpaEntity u on u.id = p.userId " +
                                where +
                                " order by p.approvedAt desc, p.id desc",
                        AdminCreditChargePaymentRow.class
                )
                .setParameter("status", CreditChargePaymentStatus.DONE)
                .setParameter("startAt", startAt)
                .setParameter("endAt", endAt)
                .setParameter("keyword", keyword)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();

        return new PageResult<>(items, total);
    }

    public record PageResult<T>(
            List<T> items,
            long totalElements
    ) {
    }

    public record AdminCreditChargePaymentRow(
            Long id,
            String orderNo,
            String loginId,
            Long chargedCredit,
            String paymentMethod,
            Long paidAmount,
            LocalDateTime approvedAt
    ) {
    }
}