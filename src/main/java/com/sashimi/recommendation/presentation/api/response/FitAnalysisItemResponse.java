package com.sashimi.recommendation.presentation.api.response;

import com.sashimi.recommendation.domain.model.FitAnalysisCategory;
import com.sashimi.recommendation.domain.model.FitAnalysisItem;
import com.sashimi.recommendation.domain.model.FitStatus;

import java.util.List;

public record FitAnalysisItemResponse(
        FitAnalysisCategory category,
        FitStatus status,
        String requiredCondition,
        String userCondition,
        String comment,
        List<String> missingItems
) {
    public static FitAnalysisItemResponse from(FitAnalysisItem item) {
        if (item == null) {
            return null;
        }

        return new FitAnalysisItemResponse(
                item.category(),
                item.status(),
                item.requiredCondition(),
                item.userCondition(),
                item.comment(),
                item.missingItems()
        );
    }
}