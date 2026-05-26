package com.sashimi.payment.infrastructure.persistence;

import com.sashimi.payment.domain.model.OrderItem;
import jakarta.persistence.*;

import java.math.BigDecimal;

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
    private BigDecimal price;

    @Column(name = "discount_amount", nullable = false)
    private BigDecimal discountAmount;

    @Column(name = "final_price", nullable = false)
    private BigDecimal finalPrice;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    protected OrderItemJpaEntity() {
    }

    private OrderItemJpaEntity(String courseTitle, BigDecimal price, BigDecimal discountAmount,
                               BigDecimal finalPrice, Long orderId, Long courseId) {
        this.courseTitle = courseTitle;
        this.price = price;
        this.discountAmount = discountAmount;
        this.finalPrice = finalPrice;
        this.orderId = orderId;
        this.courseId = courseId;
    }

    public static OrderItemJpaEntity from(OrderItem orderItem) {
        return new OrderItemJpaEntity(orderItem.getCourseTitle(), orderItem.getPrice(),
                orderItem.getDiscountAmount(), orderItem.getFinalPrice(),
                orderItem.getOrderId(), orderItem.getCourseId());
    }

    public OrderItem toDomain() {
        return OrderItem.restore(id, courseTitle, price, discountAmount, finalPrice, orderId, courseId);
    }
}