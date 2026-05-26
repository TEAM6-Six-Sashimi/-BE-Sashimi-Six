package com.sashimi.recommendation.application.port;

import com.sashimi.recommendation.domain.model.CertificateRecommendation;
import com.sashimi.recommendation.domain.model.CourseRecommendation;
import com.sashimi.recommendation.domain.model.RequiredSkillRecommendation;

import java.util.List;

public record JobPostingRecommendationAnalyzeResult(
        String jobTitle,
        Integer matchRate,
        List<RequiredSkillRecommendation> requiredSkills,
        List<CourseRecommendation> courses,
        List<CertificateRecommendation> certificates
) {
}
