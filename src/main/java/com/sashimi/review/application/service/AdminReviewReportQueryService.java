package com.sashimi.review.application.service;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.review.application.port.ReviewCoursePort;
import com.sashimi.review.application.port.ReviewUserPort;
import com.sashimi.review.application.usecase.AdminReviewReportQueryUseCase;
import com.sashimi.review.domain.model.Review;
import com.sashimi.review.domain.model.ReviewReport;
import com.sashimi.review.domain.model.ReviewReportStatus;
import com.sashimi.review.domain.repository.ReviewReportRepository;
import com.sashimi.review.domain.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminReviewReportQueryService implements AdminReviewReportQueryUseCase {

    private final ReviewReportRepository reviewReportRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewCoursePort reviewCoursePort;
    private final ReviewUserPort reviewUserPort;

    @Override
    public List<ReportSummary> getReports(ReviewReportStatus status) {
        List<ReviewReport> reports = status == null
                ? reviewReportRepository.findAll()
                : reviewReportRepository.findAllByStatus(status);

        List<Long> reviewIds = reports.stream().map(ReviewReport::getReviewId).distinct().toList();
        Map<Long, Review> reviewsById = reviewRepository.findAllByIdIn(reviewIds).stream()
                .collect(Collectors.toMap(Review::getId, Function.identity()));

        List<Long> courseIds = reviewsById.values().stream().map(Review::getCourseId).distinct().toList();
        List<Long> writerIds = reviewsById.values().stream().map(Review::getUserId).distinct().toList();
        Map<Long, String> courseNamesById = reviewCoursePort.getCourseNamesBatch(courseIds);
        Map<Long, String> loginIdsByUserId = reviewUserPort.getUserLoginIdsBatch(writerIds);

        return reports.stream()
                .map(report -> {
                    Review review = reviewsById.get(report.getReviewId());
                    if (review == null) {
                        throw new BusinessException(ErrorCode.REVIEW_NOT_FOUND);
                    }
                    return new ReportSummary(
                            report.getId(),
                            review.getContent(),
                            courseNamesById.get(review.getCourseId()),
                            loginIdsByUserId.get(review.getUserId()),
                            report.getCategory(),
                            report.getCreatedAt(),
                            report.getStatus()
                    );
                })
                .toList();
    }

    @Override
    public ReportDetail getReport(Long reportId) {
        ReviewReport report = reviewReportRepository.findById(reportId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        Review review = reviewRepository.findById(report.getReviewId())
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        String writerLoginId = reviewUserPort.getUserLoginId(review.getUserId());
        String reporterLoginId = reviewUserPort.getUserLoginId(report.getReporterId());

        return new ReportDetail(
                review.getContent(),
                writerLoginId,
                reporterLoginId,
                report.getCreatedAt(),
                report.getCategory(),
                report.getReason(),
                review.getStatus(),
                report.getStatus()
        );
    }
}
