package com.sashimi.enrollment.presentation.api.response;

import com.sashimi.course.domain.model.CourseDifficulty;
import com.sashimi.enrollment.application.port.CourseDetailInfo;
import com.sashimi.enrollment.application.query.EnrolledCourseDetailView;

import java.math.BigDecimal;
import java.util.List;

public record EnrolledCourseDetailResponse(
        Long courseId,
        String title,
        String description,
        Long price,
        CourseDifficulty difficulty,
        String thumbnail,
        int totalDuration,
        BigDecimal ratingAvg,
        int reviewCount,
        int studentCount,
        String instructorName,
        String categoryName,
        BigDecimal progressRate,
        boolean completed,
        List<SessionResponse> sessions
) {
    public record SessionResponse(
            Long sessionId,
            String sessionUid,
            String title,
            String videoUrl,
            int durationSeconds,
            int sessionOrder,
            boolean preview,
            String attachmentName,
            String attachmentUrl,
            String attachmentType,
            Long attachmentSize
    ) {}

    public static EnrolledCourseDetailResponse from(EnrolledCourseDetailView view) {
        CourseDetailInfo course = view.course();
        List<SessionResponse> sessions = course.sessions().stream()
                .map(s -> new SessionResponse(
                        s.sessionId(), s.sessionUid(), s.title(), s.videoUrl(),
                        s.durationSeconds(), s.sessionOrder(), s.preview(),
                        s.attachmentName(), s.attachmentUrl(),
                        s.attachmentType(), s.attachmentSize()
                ))
                .toList();
        return new EnrolledCourseDetailResponse(
                course.courseId(), course.title(), course.description(),
                course.price(), course.difficulty(), course.thumbnail(),
                course.totalDuration(), course.ratingAvg(), course.reviewCount(),
                course.studentCount(), course.instructorName(), course.categoryName(),
                view.progressRate(), view.completed(), sessions
        );
    }
}