package com.sashimi.review.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataReviewRepository extends JpaRepository<ReviewJpaEntity, Long> {

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);
}
