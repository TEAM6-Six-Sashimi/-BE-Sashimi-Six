package com.sashimi.category.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataCategoryRepository extends JpaRepository<CategoryJpaEntity, Long> {
    List<CategoryJpaEntity> findAllByActiveTrueOrderBySortOrderAsc();

    long countByIdInAndActiveTrue(List<Long> ids);
}
