package com.sashimi.review.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataReviewRepository extends JpaRepository<ReviewJpaEntity, Long> {

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    List<ReviewJpaEntity> findAllByIdIn(List<Long> ids);
}
