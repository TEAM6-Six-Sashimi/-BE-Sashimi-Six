package com.sashimi.payment.domain.model;


public class OrderItem {

    private final Long id;
    private final String courseTitle;
    private final Long price;
    private final Long discountAmount;
    private final Long finalPrice;
    private final Long orderId;
    private final Long courseId;

    private OrderItem(Long id, String courseTitle, Long price, Long discountAmount,
                      Long finalPrice, Long orderId, Long courseId) {
        this.id = id;
        this.courseTitle = courseTitle;
        this.price = price;
        this.discountAmount = discountAmount;
        this.finalPrice = finalPrice;
        this.orderId = orderId;
        this.courseId = courseId;
    }

    public static OrderItem create(String courseTitle, Long price, Long orderId, Long courseId) {
        return new OrderItem(null, courseTitle, price, 0L, price, orderId, courseId);
    }

    public static OrderItem restore(Long id, String courseTitle, Long price, Long discountAmount,
                                    Long finalPrice, Long orderId, Long courseId) {
        return new OrderItem(id, courseTitle, price, discountAmount, finalPrice, orderId, courseId);
    }

    public Long getId() { return id; }
    public String getCourseTitle() { return courseTitle; }
    public Long getPrice() { return price; }
    public Long getDiscountAmount() { return discountAmount; }
    public Long getFinalPrice() { return finalPrice; }
    public Long getOrderId() { return orderId; }
    public Long getCourseId() { return courseId; }
}