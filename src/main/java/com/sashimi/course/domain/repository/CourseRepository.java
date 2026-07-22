package com.sashimi.course.domain.repository;

import com.sashimi.course.domain.model.Course;
import com.sashimi.course.domain.model.CourseStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CourseRepository {
    Course save(Course course);
    Optional<Course> findById(Long id);
    List<Course> findByInstructorIdAndStatus(Long instructorId, CourseStatus status);
    List<Course> findByInstructorIdAndStatusIn(Long instructorId, List<CourseStatus> statuses);
    List<Course> findByStatus(CourseStatus status);
    List<Course> findByStatusIn(List<CourseStatus> statuses);
    List<Course> findByStatusAndCategoryId(CourseStatus status, Long categoryId);
    List<Course> findByStatusAndCategoryIdIn(CourseStatus status, List<Long> categoryIds);
    List<Course> findByStatusAndIdIn(CourseStatus status, List<Long> ids);
    List<Course> findByStatusAndApprovedAtBefore(CourseStatus status, LocalDateTime cutoff);
    List<Course> findByStatusAndArchivedFalse(CourseStatus status);
    List<Course> searchApprovedByKeyword(
            String keyword,
            int limit
    );

    List<Course> findPopularApprovedCourses(
            int limit
    );
    void deleteById(Long id);
}