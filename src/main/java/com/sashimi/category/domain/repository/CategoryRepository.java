package com.sashimi.category.domain.repository;

import com.sashimi.category.domain.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    List<Category> findAllActive();
    List<Category> findByName(String name);
    boolean existsAllActiveByIds(List<Long> categoryIds);
    Optional<Category> findBySubCategory(String subCategory);
    Optional<Category> findById(Long id);
}
