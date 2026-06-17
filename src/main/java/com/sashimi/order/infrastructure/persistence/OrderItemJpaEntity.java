package com.sashimi.order.infrastructure.persistence;

import com.sashimi.order.domain.model.OrderItem;
import com.sashimi.order.domain.model.OrderItemType;
import jakarta.persistence.*;


@Entity
@Table(name = "order_items")
public class OrderItemJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @Column(name = "course_title", nullable = false)
    private String courseTitle;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "discount_amount", nullable = false)
    private Long discountAmount;

    @Column(name = "final_price", nullable = false)
    private Long finalPrice;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "course_id")
    private Long courseId;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private OrderItemType itemType;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    protected OrderItemJpaEntity() {
    }

    private OrderItemJpaEntity(OrderItemType itemType, Long itemId, String courseTitle, Long price,
                               Long discountAmount, Long finalPrice, Long orderId, Long courseId) {
        this.courseTitle = courseTitle;
        this.price = price;
        this.discountAmount = discountAmount;
        this.finalPrice = finalPrice;
        this.orderId = orderId;
        this.courseId = courseId;
        this.itemType = itemType;
        this.itemId = itemId;
    }

    public static OrderItemJpaEntity from(OrderItem orderItem) {
        OrderItemJpaEntity entity = new OrderItemJpaEntity(
                orderItem.getItemType(),
                orderItem.getItemId(),
                orderItem.getCourseTitle(),
                orderItem.getPrice(),
                orderItem.getDiscountAmount(),
                orderItem.getFinalPrice(),
                orderItem.getOrderId(),
                orderItem.getCourseId()
        );
        entity.id = orderItem.getId();
        return entity;
    }

    public OrderItem toDomain() {
        return OrderItem.restore(id, itemType, itemId, courseTitle, price, discountAmount, finalPrice, orderId, courseId);
    }
}