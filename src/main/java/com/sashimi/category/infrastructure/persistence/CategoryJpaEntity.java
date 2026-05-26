package com.sashimi.category.infrastructure.persistence;

import com.sashimi.category.domain.model.Category;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "categories")
public class CategoryJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "sub_category", nullable = false)
    private String subCategory;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected CategoryJpaEntity() {}

    public Category toDomain() {
        return Category.restore(id, name, subCategory, sortOrder, active, createdAt);
    }
}
