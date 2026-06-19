package com.sashimi.course.application.query;

import com.sashimi.course.domain.model.CourseDifficulty;
import com.sashimi.course.application.port.NcsInfoView;

import java.math.BigDecimal;
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
        String instructorName,
        String categoryName,
        NcsInfoView ncs,
        List<SessionView> sessions
) {
    public record SessionView(
            Long sessionId,
            String title,
            String videoUrl,
            int durationSeconds,
            int sessionOrder,
            boolean preview
    ) {}
}