package com.sashimi.recommendation.presentation.api.response;

import com.sashimi.recommendation.domain.model.RequiredSkillRecommendation;

public record RequiredSkillRecommendationResponse (
        String name,
        String category,
        Boolean matched
) {
    public static RequiredSkillRecommendationResponse from(RequiredSkillRecommendation skill) {
        return new RequiredSkillRecommendationResponse(
                skill.name(),
                skill.category(),
                skill.matched()
        );
    }
}
