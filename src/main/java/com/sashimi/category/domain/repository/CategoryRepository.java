package com.sashimi.category.domain.repository;

import com.sashimi.category.domain.model.Category;

import java.util.List;

public interface CategoryRepository {
    List<Category> findAllActive();
    List<Category> findByName(String name);
    boolean existsAllActiveByIds(List<Long> categoryIds);
}
