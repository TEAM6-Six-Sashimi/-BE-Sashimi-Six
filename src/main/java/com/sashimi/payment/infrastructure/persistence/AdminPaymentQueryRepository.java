package com.sashimi.payment.infrastructure.persistence;

import com.sashimi.order.domain.model.OrderItemType;
import com.sashimi.payment.domain.model.PaymentStatus;
import com.sashimi.subscription.domain.model.SubscriptionPlan;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AdminPaymentQueryRepository {

    private final EntityManager entityManager;

    public PageResult<AdminCoursePaymentOrderRow> findCoursePayments(
            LocalDateTime startAt,
            LocalDateTime endAt,
            String keyword,
            int page,
            int size
    ) {
        String where = """
                where p.status = :status
                  and exists (
                      select 1
                      from OrderItemJpaEntity i
                      where i.orderId = o.id
                        and i.itemType = :itemType
                  )
                  and (:startAt is null or p.paidAt >= :startAt)
                  and (:endAt is null or p.paidAt < :endAt)
                  and (
                      :keyword is null
                      or lower(u.name) like concat('%', :keyword, '%')
                      or lower(u.loginId) like concat('%', :keyword, '%')
                      or lower(o.orderNo) like concat('%', :keyword, '%')
                  )
                """;

        Long total = entityManager.createQuery(
                        "select count(distinct p.id) " +
                                "from PaymentJpaEntity p " +
                                "join OrderJpaEntity o on o.id = p.orderId " +
                                "join UserJpaEntity u on u.id = p.userId " +
                                where,
                        Long.class
                )
                .setParameter("status", PaymentStatus.PAID)
                .setParameter("itemType", OrderItemType.COURSE)
                .setParameter("startAt", startAt)
                .setParameter("endAt", endAt)
                .setParameter("keyword", keyword)
                .getSingleResult();

        List<AdminCoursePaymentOrderRow> items = entityManager.createQuery(
                        "select new com.sashimi.payment.infrastructure.persistence.AdminPaymentQueryRepository$AdminCoursePaymentOrderRow(" +
                                "p.id, o.id, o.orderNo, u.name, u.loginId, p.amount, p.paidAt" +
                                ") " +
                                "from PaymentJpaEntity p " +
                                "join OrderJpaEntity o on o.id = p.orderId " +
                                "join UserJpaEntity u on u.id = p.userId " +
                                where +
                                " order by p.paidAt desc, p.id desc",
                        AdminCoursePaymentOrderRow.class
                )
                .setParameter("status", PaymentStatus.PAID)
                .setParameter("itemType", OrderItemType.COURSE)
                .setParameter("startAt", startAt)
                .setParameter("endAt", endAt)
                .setParameter("keyword", keyword)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();

        return new PageResult<>(items, total);
    }

    public List<AdminCoursePaymentCourseRow> findCourseItems(List<Long> orderIds) {
        if (orderIds.isEmpty()) {
            return List.of();
        }

        return entityManager.createQuery(
                        """
                        select new com.sashimi.payment.infrastructure.persistence.AdminPaymentQueryRepository$AdminCoursePaymentCourseRow(
                            i.orderId,
                            i.courseTitle,
                            i.finalPrice
                        )
                        from OrderItemJpaEntity i
                        where i.orderId in :orderIds
                          and i.itemType = :itemType
                        order by i.id asc
                        """,
                        AdminCoursePaymentCourseRow.class
                )
                .setParameter("orderIds", orderIds)
                .setParameter("itemType", OrderItemType.COURSE)
                .getResultList();
    }

    public PageResult<AdminSubscriptionPaymentRow> findSubscriptionPayments(
            LocalDateTime startAt,
            LocalDateTime endAt,
            String keyword,
            SubscriptionPlan plan,
            int page,
            int size
    ) {
        String where = """
                where (:startAt is null or sp.paidAt >= :startAt)
                  and (:endAt is null or sp.paidAt < :endAt)
                  and (:plan is null or sp.plan = :plan)
                  and (
                      :keyword is null
                      or lower(u.name) like concat('%', :keyword, '%')
                      or lower(u.loginId) like concat('%', :keyword, '%')
                      or lower(sp.orderNo) like concat('%', :keyword, '%')
                  )
                """;

        Long total = entityManager.createQuery(
                        "select count(sp.id) " +
                                "from SubscriptionPaymentJpaEntity sp " +
                                "join UserJpaEntity u on u.id = sp.userId " +
                                where,
                        Long.class
                )
                .setParameter("startAt", startAt)
                .setParameter("endAt", endAt)
                .setParameter("keyword", keyword)
                .setParameter("plan", plan)
                .getSingleResult();

        List<AdminSubscriptionPaymentRow> items = entityManager.createQuery(
                        "select new com.sashimi.payment.infrastructure.persistence.AdminPaymentQueryRepository$AdminSubscriptionPaymentRow(" +
                                "sp.id, sp.orderNo, u.name, u.loginId, sp.plan, sp.amount, sp.paidAt" +
                                ") " +
                                "from SubscriptionPaymentJpaEntity sp " +
                                "join UserJpaEntity u on u.id = sp.userId " +
                                where +
                                " order by sp.paidAt desc, sp.id desc",
                        AdminSubscriptionPaymentRow.class
                )
                .setParameter("startAt", startAt)
                .setParameter("endAt", endAt)
                .setParameter("keyword", keyword)
                .setParameter("plan", plan)
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

    public record AdminCoursePaymentOrderRow(
            Long paymentId,
            Long orderId,
            String orderNo,
            String userName,
            String loginId,
            Long totalAmount,
            LocalDateTime paidAt
    ) {
    }

    public record AdminCoursePaymentCourseRow(
            Long orderId,
            String courseTitle,
            Long price
    ) {
    }

    public record AdminSubscriptionPaymentRow(
            Long paymentId,
            String orderNo,
            String userName,
            String loginId,
            SubscriptionPlan plan,
            Long amount,
            LocalDateTime paidAt
    ) {
    }
}
