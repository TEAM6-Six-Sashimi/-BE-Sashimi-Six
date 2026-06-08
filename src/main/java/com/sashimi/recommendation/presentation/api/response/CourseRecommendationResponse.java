package com.sashimi.recommendation.presentation.api.response;

import com.sashimi.recommendation.domain.model.CourseRecommendation;

public record CourseRecommendationResponse (
        Long courseId,
        String title,
        String instructor,
        String matchedSkill,
        String reason
) {
    public static CourseRecommendationResponse from(CourseRecommendation course) {
        return new CourseRecommendationResponse(
                course.courseId(),
                course.title(),
                course.instructor(),
                course.matchedSkill(),
                course.reason()
        );
    }
}
