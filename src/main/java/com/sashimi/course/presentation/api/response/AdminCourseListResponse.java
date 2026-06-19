package com.sashimi.course.presentation.api.response;

import com.sashimi.course.domain.model.Course;
import com.sashimi.course.domain.model.CourseStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AdminCourseListResponse(
        Long courseId,
        String title,
        String categoryName,
        String instructorName,
        int studentCount,
        BigDecimal ratingAvg,
        CourseStatus status,
        LocalDateTime approvedAt
) {
    public static AdminCourseListResponse of(Course course, String categoryName, String instructorName) {
        return new AdminCourseListResponse(
                course.getId(),
                course.getTitle(),
                categoryName,
                instructorName,
                course.getStudentCount(),
                course.getRatingAvg(),
                course.getStatus(),
                course.getApprovedAt()
        );
    }
}