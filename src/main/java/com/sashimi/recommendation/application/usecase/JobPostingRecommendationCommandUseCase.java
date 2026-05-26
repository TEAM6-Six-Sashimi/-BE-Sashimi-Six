package com.sashimi.recommendation.application.usecase;

import com.sashimi.recommendation.application.command.CreateJobPostingRecommendationCommand;
import com.sashimi.recommendation.domain.model.JobPostingRecommendation;

public interface JobPostingRecommendationCommandUseCase {

    JobPostingRecommendation create(CreateJobPostingRecommendationCommand command);
}