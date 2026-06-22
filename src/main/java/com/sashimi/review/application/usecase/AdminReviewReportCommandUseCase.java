package com.sashimi.review.application.usecase;

public interface AdminReviewReportCommandUseCase {

    void deleteReportedReview(Long reportId);

    void rejectReport(Long reportId);
}
