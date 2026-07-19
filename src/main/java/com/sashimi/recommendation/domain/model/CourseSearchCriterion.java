package com.sashimi.recommendation.domain.model;

import java.util.List;

public record CourseSearchCriterion(
        String recommendationType,
        String keyword,
        String reason,
        List<String> relatedSkills
) {
    public CourseSearchCriterion {
        relatedSkills = relatedSkills == null ? List.of() : relatedSkills;
    }
}