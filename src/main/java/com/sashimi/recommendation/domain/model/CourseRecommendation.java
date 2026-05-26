package com.sashimi.recommendation.domain.model;

public class CourseRecommendation {

    private final Long courseId;
    private final String title;
    private final String instructor;
    private final String matchedSkill;
    private final String reason;

    public CourseRecommendation(
            Long courseId,
            String title,
            String instructor,
            String matchedSkill,
            String reason
    ) {
        this.courseId = courseId;
        this.title = title;
        this.instructor = instructor;
        this.matchedSkill = matchedSkill;
        this.reason = reason;
    }

    public Long courseId() {
        return courseId;
    }

    public String title() {
        return title;
    }

    public String instructor() {
        return instructor;
    }

    public String matchedSkill() {
        return matchedSkill;
    }

    public String reason() {
        return reason;
    }
}
