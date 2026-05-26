package com.sashimi.course.infrastructure.persistence;

import com.sashimi.course.domain.model.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataCourseRepository extends JpaRepository<CourseJpaEntity, Long> {
    List<CourseJpaEntity> findByInstructorIdAndStatus(Long instructorId, CourseStatus status);
    List<CourseJpaEntity> findByInstructorIdAndStatusIn(Long instructorId, List<CourseStatus> statuses);
    List<CourseJpaEntity> findByStatus(CourseStatus status);
}
