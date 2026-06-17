package com.sashimi.cart.infrastructure.persistence;


import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.sashimi.cart.domain.model.CartItemType;

@Entity
    @Table(
            name = "cart_items",
            uniqueConstraints = {
                    @UniqueConstraint(
                            name = "uq_cart_user_course",
                            columnNames = {"user_id", "course_id"}
                    )
            }
    )
    public class CartItemJpaEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "cart_item_id")
        private Long id;

        @Enumerated(EnumType.STRING)
        @Column(name = "item_type", nullable = false)
        private CartItemType itemType;

        @Column(name = "item_id", nullable = false)
        private Long itemId;

        @Column(name = "price", nullable = false)
        private Long price;

        @Column(name = "selected", nullable = false)
        private boolean selected;

        @Column(name = "created_at", nullable = false)
        private LocalDateTime createdAt;

        @Column(name = "user_id", nullable = false)
        private Long userId;

        @Column(name = "course_id")
        private Long courseId;

        protected CartItemJpaEntity() {
        }

    public CartItemJpaEntity(Long userId, CartItemType itemType, Long itemId,
                             Long courseId, Long price, boolean selected, LocalDateTime createdAt) {
        this.userId = userId;
        this.itemType = itemType;
        this.itemId = itemId;
        this.courseId = courseId;
        this.price = price;
        this.selected = selected;
        this.createdAt = createdAt;
    }

    public void changeSelected(boolean selected) {
        this.selected = selected;
    }

        public Long getId() {
            return id;
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

        public Long getUserId() {
            return userId;
        }

        public Long getCourseId() {
            return courseId;
        }

        public CartItemType getItemType() {return itemType; }

        public Long getItemId() {return itemId; }
    }

