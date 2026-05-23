package com.sashimi.cart.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CartItem {

    private final Long id;
    private final Long userId;
    private final Long courseId;
    private final BigDecimal price;
    private final boolean selected;
    private final LocalDateTime createdAt;

    private CartItem(Long id, Long userId, Long courseId, BigDecimal price, boolean selected, LocalDateTime createdAt) {
        if (userId == null) {
            throw new IllegalArgumentException("User id is required.");
        }
        if (courseId == null) {
            throw new IllegalArgumentException("Course id is required.");
        }
        if (price == null) {
            throw new IllegalArgumentException("Cart item price is required.");
        }
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Cart item price cannot be negative.");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Created at is required.");
        }

        this.id = id;
        this.userId = userId;
        this.courseId = courseId;
        this.price = price;
        this.selected = selected;
        this.createdAt = createdAt;
    }

    public static CartItem create(Long userId, Long courseId, BigDecimal price) {
        return new CartItem(null, userId, courseId, price, true, LocalDateTime.now());
    }

    public static CartItem restore(Long id, Long userId, Long courseId, BigDecimal price, boolean selected, LocalDateTime createdAt) {
        return new CartItem(id, userId, courseId, price, selected, createdAt);
    }

    public CartItem changeSelected(boolean selected) {
        return new CartItem(id, userId, courseId, price, selected, createdAt);
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getCourseId() { return courseId; }
    public BigDecimal getPrice() { return price; }
    public boolean isSelected() { return selected; }
    public LocalDateTime getCreatedAt() { return createdAt; }

}