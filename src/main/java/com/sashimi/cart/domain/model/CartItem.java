package com.sashimi.cart.domain.model;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;

import java.time.LocalDateTime;

public class CartItem {

    private final Long id;
    private final Long userId;
    private final CartItemType itemType;
    private final Long itemId;
    private final Long courseId;
    private final Long price;
    private final boolean selected;
    private final LocalDateTime createdAt;

    private CartItem(Long id, Long userId, CartItemType itemType, Long itemId,
                     Long courseId, Long price, boolean selected, LocalDateTime createdAt) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        if (itemType == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        if (itemId == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        if (itemType == CartItemType.COURSE && courseId == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        if (price == null || price < 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        if (createdAt == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        this.id = id;
        this.userId = userId;
        this.itemType = itemType;
        this.itemId = itemId;
        this.courseId = courseId;
        this.price = price;
        this.selected = selected;
        this.createdAt = createdAt;
    }

    public static CartItem create(Long userId, Long courseId, Long price) {
        return createCourse(userId, courseId, price);
    }

    public static CartItem createCourse(Long userId, Long courseId, Long price) {
        return new CartItem(null, userId, CartItemType.COURSE, courseId, courseId, price, true, LocalDateTime.now());
    }

    public static CartItem createSubscription(Long userId, Long subscriptionPlanId, Long price) {
        return new CartItem(null, userId, CartItemType.AI_SUBSCRIPTION, subscriptionPlanId, null, price, true, LocalDateTime.now());
    }

    public static CartItem restore(Long id, Long userId, Long courseId, Long price, boolean selected, LocalDateTime createdAt) {
        return new CartItem(id, userId, CartItemType.COURSE, courseId, courseId, price, selected, createdAt);
    }

    public static CartItem restore(Long id, Long userId, CartItemType itemType, Long itemId,
                                   Long courseId, Long price, boolean selected, LocalDateTime createdAt) {
        return new CartItem(id, userId, itemType, itemId, courseId, price, selected, createdAt);
    }

    public CartItem changeSelected(boolean selected) {
        return new CartItem(id, userId, itemType, itemId, courseId, price, selected, createdAt);
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getCourseId() { return courseId; }
    public Long getPrice() { return price; }
    public boolean isSelected() { return selected; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public CartItemType getItemType() { return itemType; }
    public Long getItemId() { return itemId; }

}