package com.sashimi.review.domain.repository;

import com.sashimi.review.domain.model.Review;

public interface ReviewRepository {

    Review save(Review review);

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);
}
