package com.sashimi.category.infrastructure.persistence;

import com.sashimi.category.domain.model.Category;
import com.sashimi.category.domain.repository.CategoryRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    @Override
    public List<Category> findByName(String name) {
        return springDataCategoryRepository.findByNameAndActiveTrueOrderBySortOrderAsc(name)
                .stream()
                .map(CategoryJpaEntity::toDomain)
                .toList();
    }
    @Override
    public Optional<Category> findBySubCategory(String subCategory) {
        return springDataCategoryRepository.findBySubCategoryAndActiveTrue(subCategory)
                .map(CategoryJpaEntity::toDomain);
    }

    @Override
    public Optional<Category> findById(Long id) {
        return springDataCategoryRepository.findById(id)
                .map(CategoryJpaEntity::toDomain);
    }

    @Override
    public boolean existsAllActiveByIds(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return true;
        }

        return springDataCategoryRepository.countByIdInAndActiveTrue(categoryIds) == categoryIds.size();
    }
}
