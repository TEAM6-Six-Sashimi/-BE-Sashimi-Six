package com.sashimi.recommendation.domain.model;

import java.util.List;

public class FitAnalysisItem {

    private final FitAnalysisCategory category;
    private final FitStatus status;
    private final String requiredCondition;
    private final String userCondition;
    private final String comment;
    private final List<String> missingItems;

    public FitAnalysisItem(
            FitAnalysisCategory category,
            FitStatus status,
            String requiredCondition,
            String userCondition,
            String comment,
            List<String> missingItems
    ) {
        this.category = category;
        this.status = status;
        this.requiredCondition = requiredCondition;
        this.userCondition = userCondition;
        this.comment = comment;
        this.missingItems = missingItems == null ? List.of() : missingItems;
    }

    public FitAnalysisCategory category() {
        return category;
    }

    public FitStatus status() {
        return status;
    }

    public String requiredCondition() {
        return requiredCondition;
    }

    public String userCondition() {
        return userCondition;
    }

    public String comment() {
        return comment;
    }

    public List<String> missingItems() {
        return missingItems;
    }
}