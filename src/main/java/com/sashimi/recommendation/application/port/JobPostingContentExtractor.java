package com.sashimi.recommendation.application.port;

import com.sashimi.recommendation.domain.model.RecommendationInputType;

public interface JobPostingContentExtractor {

    String extract(
            RecommendationInputType inputType,
            String sourceUrl,
            String rawContent
    );
}