package com.sashimi.review.infrastructure.persistence;

import com.sashimi.review.domain.model.Review;
import com.sashimi.review.domain.model.ReviewStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class ReviewJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReviewStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected ReviewJpaEntity() {}

    public ReviewJpaEntity(Long userId, Long courseId, int rating, String content,
                           ReviewStatus status, LocalDateTime createdAt) {
        this.userId = userId;
        this.courseId = courseId;
        this.rating = rating;
        this.content = content;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Review toDomain() {
        return Review.restore(id, userId, courseId, rating, content, status, createdAt);
    }

    public static ReviewJpaEntity fromDomain(Review review) {
        return new ReviewJpaEntity(
                review.getUserId(), review.getCourseId(), review.getRating(),
                review.getContent(), review.getStatus(), review.getCreatedAt()
        );
    }

    public Long getId() { return id; }
}
