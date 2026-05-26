package com.sashimi.recommendation.application.usecase;

import com.sashimi.recommendation.domain.model.CertificateRecommendation;
import com.sashimi.recommendation.domain.model.CourseRecommendation;
import com.sashimi.recommendation.domain.model.JobPostingRecommendation;
import com.sashimi.recommendation.domain.model.RequiredSkillRecommendation;

import java.util.List;

public interface JobPostingRecommendationQueryUseCase {

    JobPostingRecommendation getLatest(Long userId);

    List<RequiredSkillRecommendation> getLatestSkills(Long userId);

    List<CourseRecommendation> getLatestCourses(Long userId);

    List<CertificateRecommendation> getLatestCertificates(Long userId);
}