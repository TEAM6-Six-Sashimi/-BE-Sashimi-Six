package com.sashimi.dashboard.application.port;

import java.time.LocalDateTime;

public interface InstructorDashboardQueryPort {

    Long sumMonthlyCourseSalesByInstructor(
            Long instructorId,
            LocalDateTime startAt,
            LocalDateTime endAt
    );
}