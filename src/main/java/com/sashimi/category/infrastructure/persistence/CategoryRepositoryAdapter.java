package com.sashimi.category.infrastructure.persistence;

import com.sashimi.category.domain.model.Category;
import com.sashimi.category.domain.repository.CategoryRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoryRepositoryAdapter implements CategoryRepository {

    private final SpringDataCategoryRepository springDataCategoryRepository;

    public CategoryRepositoryAdapter(SpringDataCategoryRepository springDataCategoryRepository) {
        this.springDataCategoryRepository = springDataCategoryRepository;
    }

    @Override
    public List<Category> findAllActive() {
        return springDataCategoryRepository.findAllByActiveTrueOrderBySortOrderAsc()
                .stream()
                .map(CategoryJpaEntity::toDomain)
                .toList();
    }
}