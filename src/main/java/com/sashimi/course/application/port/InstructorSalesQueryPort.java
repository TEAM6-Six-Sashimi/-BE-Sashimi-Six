package com.sashimi.course.application.port;

import java.time.LocalDateTime;
import java.util.List;

public interface InstructorSalesQueryPort {

    List<InstructorCourseSales> findMonthlyCourseSales(
            Long instructorId,
            LocalDateTime startAt,
            LocalDateTime endAt
    );
}
