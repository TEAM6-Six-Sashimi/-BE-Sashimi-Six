package com.sashimi.course.application.port;

import java.time.LocalDateTime;
import java.util.List;

public interface CourseReviewPort {

    List<ReviewInfo> findActiveReviewsByCourseId(Long courseId);

    record ReviewInfo(Long reviewId, int rating, String content, String writerLoginId, LocalDateTime createdAt) {}
}
