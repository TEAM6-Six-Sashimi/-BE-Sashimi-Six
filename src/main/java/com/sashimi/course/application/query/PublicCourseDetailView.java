package com.sashimi.course.application.query;

import com.sashimi.course.domain.model.CourseDifficulty;
import com.sashimi.course.domain.model.CourseStatus;
import com.sashimi.course.application.port.NcsInfoView;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PublicCourseDetailView(
        CourseViewerType viewerType,
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
        InstructorView instructor,
        String mainCategoryName,
        String categoryName,
        NcsInfoView ncs,
        LocalDateTime approvedAt,
        CourseStatus status,
        String rejectReason,
        BigDecimal progressRate,
        Boolean completed,
        List<SessionView> sessions,
        List<ReviewView> reviews,
        List<RatingDistributionView> ratingDistribution
) {
    public record InstructorView(
            String name,
            String profileImagePath,
            String bio,
            List<String> mainCareers,
            String portfolioUrl
    ) {}

    public record SessionView(
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
            Long attachmentSize,
            Integer lastPositionSeconds,
            BigDecimal sessionProgressRate,
            Boolean sessionCompleted
    ) {}

    public record ReviewView(
            Long reviewId,
            int rating,
            String content,
            String writerLoginId,
            LocalDateTime createdAt
    ) {}

    public record RatingDistributionView(int star, int count) {}
}
