package com.sashimi.recommendation.application.port;

import com.sashimi.recommendation.domain.model.JobPostingRecommendation;

public interface JobPostingRecommendationAnalyzePort {

    JobPostingRecommendationAnalyzeResult analyze(JobPostingRecommendation recommendation);
}