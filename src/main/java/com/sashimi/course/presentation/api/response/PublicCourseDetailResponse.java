package com.sashimi.course.presentation.api.response;

import com.sashimi.course.application.query.CourseViewerType;
import com.sashimi.course.application.query.PublicCourseDetailView;
import com.sashimi.course.domain.model.CourseDifficulty;
import com.sashimi.course.domain.model.CourseStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PublicCourseDetailResponse(
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
        InstructorResponse instructor,
        String mainCategoryName,
        String categoryName,
        NcsInfoResponse ncs,
        LocalDateTime approvedAt,
        CourseStatus status,
        String rejectReason,
        BigDecimal progressRate,
        Boolean completed,
        List<SessionResponse> sessions,
        List<ReviewResponse> reviews,
        List<RatingDistributionResponse> ratingDistribution
) {
    public record InstructorResponse(
            String name,
            String profileImagePath,
            String bio,
            List<String> mainCareers,
            String portfolioUrl
    ) {}

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
            Long attachmentSize,
            Integer lastPositionSeconds,
            BigDecimal sessionProgressRate,
            Boolean sessionCompleted
    ) {}

    public record NcsInfoResponse(
            String categoryPath,
            String jobDescription,
            List<String> abilityUnitNames,
            int totalAbilityUnitCount
    ) {}

    public record ReviewResponse(
            Long reviewId,
            int rating,
            String content,
            String writerLoginId,
            LocalDateTime createdAt
    ) {}

    public record RatingDistributionResponse(int star, int count) {}

    public static PublicCourseDetailResponse from(PublicCourseDetailView view) {
        InstructorResponse instructor = new InstructorResponse(
                view.instructor().name(),
                view.instructor().profileImagePath(),
                view.instructor().bio(),
                view.instructor().mainCareers(),
                view.instructor().portfolioUrl()
        );

        NcsInfoResponse ncs = view.ncs() == null ? null : new NcsInfoResponse(
                view.ncs().categoryPath(),
                view.ncs().jobDescription(),
                view.ncs().abilityUnitNames(),
                view.ncs().totalAbilityUnitCount()
        );

        List<SessionResponse> sessions = view.sessions().stream()
                .map(s -> new SessionResponse(
                        s.sessionId(), s.sessionUid(), s.title(), s.videoUrl(),
                        s.durationSeconds(), s.sessionOrder(), s.preview(),
                        s.attachmentName(), s.attachmentUrl(),
                        s.attachmentType(), s.attachmentSize(),
                        s.lastPositionSeconds(), s.sessionProgressRate(), s.sessionCompleted()
                ))
                .toList();

        List<ReviewResponse> reviews = view.reviews().stream()
                .map(r -> new ReviewResponse(
                        r.reviewId(), r.rating(), r.content(),
                        r.writerLoginId(), r.createdAt()
                ))
                .toList();

        List<RatingDistributionResponse> ratingDistribution = view.ratingDistribution().stream()
                .map(d -> new RatingDistributionResponse(d.star(), d.count()))
                .toList();

        return new PublicCourseDetailResponse(
                view.viewerType(), view.courseId(), view.title(), view.description(),
                view.price(), view.difficulty(), view.thumbnail(),
                view.totalDuration(), view.ratingAvg(), view.reviewCount(),
                view.studentCount(), instructor, view.mainCategoryName(),
                view.categoryName(), ncs, view.approvedAt(),
                view.status(), view.rejectReason(), view.progressRate(), view.completed(),
                sessions, reviews, ratingDistribution
        );
    }
}
