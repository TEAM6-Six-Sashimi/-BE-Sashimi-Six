package com.sashimi.course.application.query;

import com.sashimi.course.domain.model.CourseDifficulty;
import com.sashimi.course.application.port.NcsInfoView;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PublicCourseDetailView(
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
        List<SessionView> sessions,
        List<ReviewView> reviews
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
            String title,
            String videoUrl,
            int durationSeconds,
            int sessionOrder,
            boolean preview
    ) {}

    public record ReviewView(
            Long reviewId,
            int rating,
            String content,
            String writerLoginId,
            LocalDateTime createdAt
    ) {}
}