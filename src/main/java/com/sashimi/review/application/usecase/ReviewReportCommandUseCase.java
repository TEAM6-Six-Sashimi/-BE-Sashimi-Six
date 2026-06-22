package com.sashimi.review.application.usecase;

import com.sashimi.review.application.command.ReportReviewCommand;

public interface ReviewReportCommandUseCase {

    void reportReview(ReportReviewCommand command);
}
