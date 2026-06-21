package com.sashimi.review.infrastructure.persistence;

import com.sashimi.review.domain.model.Review;
import com.sashimi.review.domain.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryAdapter implements ReviewRepository {

    private final SpringDataReviewRepository springDataReviewRepository;

    @Override
    public Review save(Review review) {
        ReviewJpaEntity entity = ReviewJpaEntity.fromDomain(review);
        return springDataReviewRepository.save(entity).toDomain();
    }

    @Override
    public boolean existsByUserIdAndCourseId(Long userId, Long courseId) {
        return springDataReviewRepository.existsByUserIdAndCourseId(userId, courseId);
    }
}
