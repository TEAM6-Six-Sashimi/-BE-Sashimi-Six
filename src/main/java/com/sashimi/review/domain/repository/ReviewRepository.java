package com.sashimi.review.domain.repository;

import com.sashimi.review.domain.model.Review;

import java.util.Optional;

public interface ReviewRepository {

    Review save(Review review);

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    Optional<Review> findById(Long reviewId);
}
