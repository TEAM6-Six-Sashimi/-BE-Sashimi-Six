package com.sashimi.course.application.port;

import java.time.LocalDateTime;

public interface CourseEnrollmentPort {

    /** 해당 강의에 아직 시청 가능한(수강 등록일이 cutoff 이후인) 학생이 있는지 */
    boolean hasActiveEnrollment(Long courseId, LocalDateTime cutoff);
}
