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

    Category save(Category category);
    List<Category> findAll();
    boolean existsBySubCategory(String subCategory);
    Optional<Long> findMainCategoryIdByName(String name);
    Long findMaxMainCategoryId();
    int findMaxSortOrder();
    List<Category> findAllByIdIn(List<Long> ids);
    boolean existsByMainCategoryId(Long mainCategoryId);
    List<Category> findAllByMainCategoryIdIn(List<Long> mainCategoryIds);
}
