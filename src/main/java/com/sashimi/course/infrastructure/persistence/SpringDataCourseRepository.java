package com.sashimi.course.infrastructure.persistence;

import com.sashimi.course.application.port.InstructorCourseSales;
import com.sashimi.course.domain.model.CourseStatus;
import com.sashimi.order.domain.model.OrderItemType;
import com.sashimi.payment.domain.model.PaymentStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SpringDataCourseRepository extends JpaRepository<CourseJpaEntity, Long> {
    List<CourseJpaEntity> findByInstructorIdAndStatus(Long instructorId, CourseStatus status);
    List<CourseJpaEntity> findByInstructorIdAndStatusIn(Long instructorId, List<CourseStatus> statuses);
    List<CourseJpaEntity> findByStatus(CourseStatus status);
    List<CourseJpaEntity> findByStatusIn(List<CourseStatus> statuses);
    List<CourseJpaEntity> findByStatusAndCategoryId(CourseStatus status, Long categoryId);
    List<CourseJpaEntity> findByStatusAndCategoryIdIn(CourseStatus status, List<Long> categoryIds);
    List<CourseJpaEntity> findByStatusAndIdIn(CourseStatus status, List<Long> ids);
    List<CourseJpaEntity> findByStatusAndApprovedAtBefore(CourseStatus status, LocalDateTime approvedAt);
    List<CourseJpaEntity> findByStatusAndArchivedFalse(CourseStatus status);

    @Query("""
            select c
            from CourseJpaEntity c
            where c.status = com.sashimi.course.domain.model.CourseStatus.APPROVED
              and (
                    lower(replace(coalesce(c.title, ''), ' ', '')) like concat('%', :keyword, '%')
                 or lower(replace(coalesce(c.description, ''), ' ', '')) like concat('%', :keyword, '%')
              )
            order by c.id desc
            """)
    List<CourseJpaEntity> searchApprovedByKeyword(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("""
            select new com.sashimi.course.application.port.InstructorCourseSales(
                c.id,
                c.title,
                sum(oi.finalPrice)
            )
            from OrderItemJpaEntity oi
            join PaymentJpaEntity p on p.orderId = oi.orderId
            join CourseJpaEntity c on c.id = oi.itemId
            where c.instructorId = :instructorId
              and oi.itemType = :itemType
              and p.status = :paymentStatus
              and p.paidAt >= :startAt
              and p.paidAt < :endAt
            group by c.id, c.title
            order by sum(oi.finalPrice) desc, c.id asc
            """)
    List<InstructorCourseSales> findMonthlyCourseSalesByInstructor(
            @Param("instructorId") Long instructorId,
            @Param("itemType") OrderItemType itemType,
            @Param("paymentStatus") PaymentStatus paymentStatus,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt
    );
}