package com.sashimi.enrollment.application.port;

import com.sashimi.course.domain.model.CourseDifficulty;

import java.math.BigDecimal;
import java.util.List;

public record CourseDetailInfo(
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
        List<SessionInfo> sessions
) {
    public record SessionInfo(
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
}
