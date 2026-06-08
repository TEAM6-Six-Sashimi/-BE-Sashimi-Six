package com.sashimi.category.application.usecase;

import com.sashimi.category.domain.model.Category;

import java.util.List;

public interface CategoryQueryUseCase {
    List<Category> getActiveCategories();
}
