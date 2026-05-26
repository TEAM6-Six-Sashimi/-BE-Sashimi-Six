package com.sashimi.category.application.service;

import com.sashimi.category.application.usecase.CategoryQueryUseCase;
import com.sashimi.category.domain.model.Category;
import com.sashimi.category.domain.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryQueryService implements CategoryQueryUseCase {

    private final CategoryRepository categoryRepository;

    public CategoryQueryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getActiveCategories() {
        return categoryRepository.findAllActive();
    }
}
