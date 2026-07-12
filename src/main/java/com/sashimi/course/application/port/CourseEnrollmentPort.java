package com.sashimi.course.application.port;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface CourseEnrollmentPort {

    /** 해당 강의에 아직 시청 가능한(수강 등록일이 cutoff 이후인) 학생이 있는지 */
    boolean hasActiveEnrollment(Long courseId, LocalDateTime cutoff);

    /**
     * 강의별 완강생(is_completed = true) 수를 한 번에 집계한다.
     * @return courseId -> 완강생 수 (완강생이 0명인 강의는 맵에 없음)
     */
    Map<Long, Integer> countCompletedByCourseIds(List<Long> courseIds);

    Map<Long, Integer> countStudentsByCourseIds(List<Long> courseIds);
}
