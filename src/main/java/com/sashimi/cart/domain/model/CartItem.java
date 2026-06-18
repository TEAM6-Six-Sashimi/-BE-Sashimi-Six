package com.sashimi.cart.domain.model;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;

import java.time.LocalDateTime;

public class CartItem {

    private final Long id;
    private final Long userId;
    private final Long courseId;
    private final Long price;
    private final boolean selected;
    private final LocalDateTime createdAt;

    private CartItem(
            Long id,
            Long userId,
            Long courseId,
            Long price,
            boolean selected,
            LocalDateTime createdAt
    ) {
        if (userId == null || courseId == null || createdAt == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (price == null || price < 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        this.id = id;
        this.userId = userId;
        this.courseId = courseId;
        this.price = price;
        this.selected = selected;
        this.createdAt = createdAt;
    }

    public static CartItem create(Long userId, Long courseId, Long price) {
        return new CartItem(
                null,
                userId,
                courseId,
                price,
                true,
                LocalDateTime.now()
        );
    }

    public static CartItem restore(
            Long id,
            Long userId,
            Long courseId,
            Long price,
            boolean selected,
            LocalDateTime createdAt
    ) {
        return new CartItem(
                id,
                userId,
                courseId,
                price,
                selected,
                createdAt
        );
    }

    public CartItem changeSelected(boolean selected) {
        return new CartItem(
                id,
                userId,
                courseId,
                price,
                selected,
                createdAt
        );
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public Long getPrice() {
        return price;
    }

    public boolean isSelected() {
        return selected;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}