package com.sashimi.review.infrastructure.persistence;

import com.sashimi.review.domain.model.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataReviewRepository extends JpaRepository<ReviewJpaEntity, Long> {

    boolean existsByUserIdAndCourseIdAndStatus(Long userId, Long courseId, ReviewStatus status);
}
