package com.sashimi.course.presentation.api.response;

import com.sashimi.course.domain.model.Course;
import com.sashimi.course.domain.model.CourseDifficulty;
import com.sashimi.course.domain.model.CourseStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CourseResponse(
        Long courseId,
        Long categoryId,
        String title,
        String description,
        BigDecimal price,
        CourseDifficulty difficulty,
        String thumbnail,
        int totalDuration,
        CourseStatus status,
        String rejectReason,
        BigDecimal ratingAvg,
        int reviewCount,
        int studentCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime approvedAt,
        List<CourseSessionResponse> sessions
) {
    public static CourseResponse from(Course course) {
        return new CourseResponse(
                course.getId(), course.getCategoryId(), course.getTitle(),
                course.getDescription(), course.getPrice(), course.getDifficulty(),
                course.getThumbnail(), course.getTotalDuration(), course.getStatus(),
                course.getRejectReason(), course.getRatingAvg(), course.getReviewCount(),
                course.getStudentCount(), course.getCreatedAt(), course.getUpdatedAt(),
                course.getApprovedAt(),
                course.getSessions().stream().map(CourseSessionResponse::from).toList()
        );
    }
}
