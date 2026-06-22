package com.sashimi.review.domain.model;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;

import java.time.LocalDateTime;

public class Review {

    private final Long id;
    private final Long userId;
    private final Long courseId;
    private final int rating;
    private final String content;
    private final ReviewStatus status;
    private final LocalDateTime createdAt;

    private Review(Long id, Long userId, Long courseId, int rating, String content,
                   ReviewStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.courseId = courseId;
        this.rating = rating;
        this.content = content;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Review create(Long userId, Long courseId, int rating, String content) {
        if (rating < 1 || rating > 5) throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        if (content != null && content.length() > 200) throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        return new Review(null, userId, courseId, rating, content, ReviewStatus.ACTIVE, LocalDateTime.now());
    }

    public static Review restore(Long id, Long userId, Long courseId, int rating, String content,
                                 ReviewStatus status, LocalDateTime createdAt) {
        return new Review(id, userId, courseId, rating, content, status, createdAt);
    }

    public Review delete() {
        return new Review(id, userId, courseId, rating, content, ReviewStatus.DELETED, createdAt);
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getCourseId() { return courseId; }
    public int getRating() { return rating; }
    public String getContent() { return content; }
    public ReviewStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
