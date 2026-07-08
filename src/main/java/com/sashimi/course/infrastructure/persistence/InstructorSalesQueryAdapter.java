package com.sashimi.course.infrastructure.persistence;

import com.sashimi.course.application.port.InstructorCourseSales;
import com.sashimi.course.application.port.InstructorSalesQueryPort;
import com.sashimi.order.domain.model.OrderItemType;
import com.sashimi.payment.domain.model.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class InstructorSalesQueryAdapter implements InstructorSalesQueryPort {

    private final SpringDataCourseRepository springDataCourseRepository;

    @Override
    public List<InstructorCourseSales> findMonthlyCourseSales(
            Long instructorId,
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {
        return springDataCourseRepository.findMonthlyCourseSalesByInstructor(
                instructorId,
                OrderItemType.COURSE,
                PaymentStatus.PAID,
                startAt,
                endAt
        );
    }
}