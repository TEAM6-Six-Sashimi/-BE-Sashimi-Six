package com.sashimi.course.infrastructure.persistence;

import com.sashimi.category.domain.repository.CategoryRepository;
import com.sashimi.course.application.port.CategoryPort;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryPortAdapter implements CategoryPort {

    private final CategoryRepository categoryRepository;

    public CategoryPortAdapter(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Long> getCategoryIdsByName(String categoryName) {
        return categoryRepository.findByName(categoryName)
                .stream()
                .map(category -> category.getId())
                .toList();
    }

    @Override
    public Long getCategoryIdBySubCategoryName(String subCategoryName) {
        return categoryRepository.findBySubCategory(subCategoryName)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND))
                .getId();
    }

    @Override
    public String getCategoryNameById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND))
                .getSubCategory();
    }
}