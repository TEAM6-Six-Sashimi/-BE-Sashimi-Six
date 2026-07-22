package com.sashimi.category.infrastructure.persistence;

import com.sashimi.category.domain.model.Category;
import com.sashimi.category.domain.repository.CategoryRepository;
import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
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

    @Override
    public Category save(Category category) {
        if (category.getId() == null) {
            CategoryJpaEntity entity = new CategoryJpaEntity(
                    category.getMainCategoryId(), category.getName(), category.getSubCategory(),
                    category.getSortOrder(), category.isActive(), category.getCreatedAt(), category.getNcsInfoId());
            return springDataCategoryRepository.save(entity).toDomain();
        }
        CategoryJpaEntity entity = springDataCategoryRepository.findById(category.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        entity.update(category.getSubCategory(), category.isActive());
        return springDataCategoryRepository.save(entity).toDomain();
    }

    @Override
    public List<Category> findAll() {
        return springDataCategoryRepository.findAllByOrderByMainCategoryIdAscIdAsc()
                .stream()
                .map(CategoryJpaEntity::toDomain)
                .toList();
    }

    @Override
    public boolean existsBySubCategory(String subCategory) {
        return springDataCategoryRepository.existsBySubCategory(subCategory);
    }

    @Override
    public Optional<Long> findMainCategoryIdByName(String name) {
        return springDataCategoryRepository.findFirstByName(name)
                .map(CategoryJpaEntity::getMainCategoryId);
    }

    @Override
    public Long findMaxMainCategoryId() {
        return springDataCategoryRepository.findMaxMainCategoryId();
    }

    @Override
    public int findMaxSortOrder() {
        return springDataCategoryRepository.findMaxSortOrder();
    }

    @Override
    public List<Category> findAllByIdIn(List<Long> ids) {
        return springDataCategoryRepository.findAllById(ids)
                .stream()
                .map(CategoryJpaEntity::toDomain)
                .toList();
    }

    @Override
    public boolean existsByMainCategoryId(Long mainCategoryId) {
        return springDataCategoryRepository.existsByMainCategoryId(mainCategoryId);
    }

    @Override
    public List<Category> findAllByMainCategoryIdIn(List<Long> mainCategoryIds) {
        return springDataCategoryRepository.findAllByMainCategoryIdIn(mainCategoryIds)
                .stream()
                .map(CategoryJpaEntity::toDomain)
                .toList();
    }
}
