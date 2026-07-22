package com.sashimi.payment.infrastructure.persistence;

import com.sashimi.order.domain.model.OrderItemType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataPaymentRepository
        extends JpaRepository<PaymentJpaEntity, Long> {

    List<PaymentJpaEntity>
    findAllByUserIdOrderByCreatedAtDescIdDesc(Long userId);

    @Query("""
            select p
            from PaymentJpaEntity p
            where p.userId = :userId
              and exists (
                    select 1
                    from OrderItemJpaEntity oi
                    where oi.orderId = p.orderId
                      and oi.itemType = :itemType
              )
            order by p.createdAt desc, p.id desc
            """)
    List<PaymentJpaEntity> findCoursePaymentsByUserId(
            @Param("userId") Long userId,
            @Param("itemType") OrderItemType itemType,
            Pageable pageable
    );
}