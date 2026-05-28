package com.sashimi.category.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataCategoryRepository extends JpaRepository<CategoryJpaEntity, Long> {
    List<CategoryJpaEntity> findAllByActiveTrueOrderBySortOrderAsc();
    List<CategoryJpaEntity> findByNameAndActiveTrueOrderBySortOrderAsc(String name);
    long countByIdInAndActiveTrue(List<Long> ids);
    Optional<CategoryJpaEntity> findBySubCategoryAndActiveTrue(String subCategory);
}
