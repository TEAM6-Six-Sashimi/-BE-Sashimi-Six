package com.sashimi.category.domain.model;

import java.time.LocalDateTime;

public class Category {

    private final Long id;
    private final Long mainCategoryId;
    private final String name;
    private final String subCategory;
    private final int sortOrder;
    private final boolean active;
    private final LocalDateTime createdAt;

    private Category(Long id, Long mainCategoryId, String name, String subCategory,
                     int sortOrder, boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.mainCategoryId = mainCategoryId;
        this.name = name;
        this.subCategory = subCategory;
        this.sortOrder = sortOrder;
        this.active = active;
        this.createdAt = createdAt;
    }

    public static Category restore(Long id, Long mainCategoryId, String name, String subCategory,
                                   int sortOrder, boolean active, LocalDateTime createdAt) {
        return new Category(id, mainCategoryId, name, subCategory, sortOrder, active, createdAt);
    }

    public Long getId() { return id; }
    public Long getMainCategoryId() { return mainCategoryId; }
    public String getName() { return name; }
    public String getSubCategory() { return subCategory; }
    public int getSortOrder() { return sortOrder; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
