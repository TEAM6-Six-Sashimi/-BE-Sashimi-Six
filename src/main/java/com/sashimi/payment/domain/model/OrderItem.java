package com.sashimi.payment.domain.model;

import java.math.BigDecimal;

public class OrderItem {

    private final Long id;
    private final String courseTitle;
    private final BigDecimal price;
    private final BigDecimal discountAmount;
    private final BigDecimal finalPrice;
    private final Long orderId;
    private final Long courseId;

    private OrderItem(Long id, String courseTitle, BigDecimal price, BigDecimal discountAmount,
                      BigDecimal finalPrice, Long orderId, Long courseId) {
        this.id = id;
        this.courseTitle = courseTitle;
        this.price = price;
        this.discountAmount = discountAmount;
        this.finalPrice = finalPrice;
        this.orderId = orderId;
        this.courseId = courseId;
    }

    public static OrderItem create(String courseTitle, BigDecimal price, Long orderId, Long courseId) {
        return new OrderItem(null, courseTitle, price, BigDecimal.ZERO, price, orderId, courseId);
    }

    public static OrderItem restore(Long id, String courseTitle, BigDecimal price, BigDecimal discountAmount,
                                    BigDecimal finalPrice, Long orderId, Long courseId) {
        return new OrderItem(id, courseTitle, price, discountAmount, finalPrice, orderId, courseId);
    }

    public Long getId() { return id; }
    public String getCourseTitle() { return courseTitle; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public BigDecimal getFinalPrice() { return finalPrice; }
    public Long getOrderId() { return orderId; }
    public Long getCourseId() { return courseId; }
}