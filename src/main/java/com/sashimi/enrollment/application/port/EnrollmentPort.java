package com.sashimi.enrollment.application.port;

import java.util.List;

public interface EnrollmentPort {

    boolean isEnrolled(Long userId, Long courseId);

    void enrollPaidCourse(Long userId, Long courseId, Long orderItemId);

    List<EnrollmentSummary> getEnrollmentsByUser(Long userId);
}