package com.sashimi.course.presentation.api.response;

import com.sashimi.course.application.query.PublicCourseDetailView;
import com.sashimi.course.domain.model.CourseDifficulty;

import java.math.BigDecimal;
import java.util.List;

public record PublicCourseDetailResponse(
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
        NcsInfoResponse ncs,
        List<SessionResponse> sessions
) {
    public record SessionResponse(
            Long sessionId,
            String title,
            String videoUrl,
            int durationSeconds,
            int sessionOrder,
            boolean preview
    ) {}

    public record NcsInfoResponse(
            String categoryPath,
            String jobDescription,
            List<String> abilityUnitNames,
            int totalAbilityUnitCount
    ) {}

    public static PublicCourseDetailResponse from(PublicCourseDetailView view) {
        List<SessionResponse> sessions = view.sessions().stream()
                .map(s -> new SessionResponse(
                        s.sessionId(),
                        s.title(),
                        s.videoUrl(),
                        s.durationSeconds(),
                        s.sessionOrder(),
                        s.preview()
                ))
                .toList();

        NcsInfoResponse ncs = view.ncs() == null ? null : new NcsInfoResponse(
                view.ncs().categoryPath(),
                view.ncs().jobDescription(),
                view.ncs().abilityUnitNames(),
                view.ncs().totalAbilityUnitCount()
        );

        return new PublicCourseDetailResponse(
                view.courseId(),
                view.title(),
                view.description(),
                view.price(),
                view.difficulty(),
                view.thumbnail(),
                view.totalDuration(),
                view.ratingAvg(),
                view.reviewCount(),
                view.studentCount(),
                view.instructorName(),
                view.categoryName(),
                ncs,
                sessions
        );
    }
}