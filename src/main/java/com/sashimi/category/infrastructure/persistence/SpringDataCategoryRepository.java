package com.sashimi.category.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SpringDataCategoryRepository extends JpaRepository<CategoryJpaEntity, Long> {
    List<CategoryJpaEntity> findAllByActiveTrueOrderBySortOrderAsc();
    List<CategoryJpaEntity> findByNameAndActiveTrueOrderBySortOrderAsc(String name);
    long countByIdInAndActiveTrue(List<Long> ids);
    Optional<CategoryJpaEntity> findBySubCategoryAndActiveTrue(String subCategory);

    List<CategoryJpaEntity> findAllByOrderByMainCategoryIdAscIdAsc();
    boolean existsBySubCategory(String subCategory);
    Optional<CategoryJpaEntity> findFirstByName(String name);

    @Query("select coalesce(max(c.mainCategoryId), 0) from CategoryJpaEntity c")
    Long findMaxMainCategoryId();

    @Query("select coalesce(max(c.sortOrder), 0) from CategoryJpaEntity c")
    int findMaxSortOrder();
}
