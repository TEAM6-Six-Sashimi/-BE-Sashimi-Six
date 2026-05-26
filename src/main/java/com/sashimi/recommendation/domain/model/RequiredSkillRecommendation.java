package com.sashimi.recommendation.domain.model;

public class RequiredSkillRecommendation {

    private final String name;
    private final String category;
    private final Boolean matched;

    public RequiredSkillRecommendation(String name, String category, Boolean matched) {
        this.name = name;
        this.category = category;
        this.matched = matched;
    }

    public String name() {
        return name;
    }

    public String category() {
        return category;
    }

    public Boolean matched() {
        return matched;
    }
}
