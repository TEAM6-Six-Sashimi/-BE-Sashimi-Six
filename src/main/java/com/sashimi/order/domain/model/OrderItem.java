package com.sashimi.order.domain.model;


public class OrderItem {

    private final Long id;
    private final String courseTitle;
    private final Long price;
    private final Long discountAmount;
    private final Long finalPrice;
    private final Long orderId;
    private final Long courseId;
    private final OrderItemType itemType;
    private final Long itemId;

    private OrderItem(Long id, OrderItemType itemType, Long itemId, String courseTitle, Long price,
                      Long discountAmount, Long finalPrice, Long orderId, Long courseId) {
        this.id = id;
        this.courseTitle = courseTitle;
        this.price = price;
        this.discountAmount = discountAmount;
        this.finalPrice = finalPrice;
        this.orderId = orderId;
        this.courseId = courseId;
        this.itemType = itemType;
        this.itemId = itemId;
    }

    public static OrderItem create(String courseTitle, Long price, Long orderId, Long courseId) {
        return createCourse(courseTitle, price, orderId, courseId);
    }

    public static OrderItem createCourse(String courseTitle, Long price, Long orderId, Long courseId) {
        return new OrderItem(null, OrderItemType.COURSE, courseId, courseTitle, price, 0L, price, orderId, courseId);
    }

    public static OrderItem createSubscription(String planName, Long price, Long orderId, Long subscriptionId) {
        return new OrderItem(null, OrderItemType.AI_SUBSCRIPTION, subscriptionId, planName, price, 0L, price, orderId, null);
    }

    public static OrderItem restore(Long id, String courseTitle, Long price, Long discountAmount,
                                    Long finalPrice, Long orderId, Long courseId) {
        return new OrderItem(
                id,
                OrderItemType.COURSE,
                courseId,
                courseTitle,
                price,
                discountAmount,
                finalPrice,
                orderId,
                courseId
        );
    }

    public static OrderItem restore(Long id, OrderItemType itemType, Long itemId, String courseTitle, Long price,
                                    Long discountAmount, Long finalPrice, Long orderId, Long courseId) {
        return new OrderItem(
                id,
                itemType,
                itemId,
                courseTitle,
                price,
                discountAmount,
                finalPrice,
                orderId,
                courseId
        );
    }

    public Long getId() { return id; }
    public String getCourseTitle() { return courseTitle; }
    public Long getPrice() { return price; }
    public Long getDiscountAmount() { return discountAmount; }
    public Long getFinalPrice() { return finalPrice; }
    public Long getOrderId() { return orderId; }
    public Long getCourseId() { return courseId; }
    public OrderItemType getItemType() { return itemType; }
    public Long getItemId() { return itemId; }



}