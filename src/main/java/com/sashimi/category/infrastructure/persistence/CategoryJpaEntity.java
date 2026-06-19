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

    @Column(name = "main_category_id", nullable = false)
    private Long mainCategoryId;

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

    @Column(name = "ncs_info_id")
    private Long ncsInfoId;

    protected CategoryJpaEntity() {}

    public void updateNcsInfoId(Long ncsInfoId) {
        this.ncsInfoId = ncsInfoId;
    }


    public Category toDomain() {
        return Category.restore(id, mainCategoryId, ncsInfoId, name, subCategory,
                sortOrder, active, createdAt);
    }

    public Long getId() {
        return id;
    }

    public Long getMainCategoryId() {
        return mainCategoryId;
    }

    public Long getNcsInfoId() {
        return ncsInfoId;
    }

    public String getName() {
        return name;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}