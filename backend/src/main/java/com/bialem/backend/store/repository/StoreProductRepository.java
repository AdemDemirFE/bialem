package com.bialem.backend.store.repository;

import com.bialem.backend.store.domain.StoreProduct;
import com.bialem.backend.store.domain.enumeration.StoreProductStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreProductRepository extends JpaRepository<StoreProduct, Long>, JpaSpecificationExecutor<StoreProduct> {
    Optional<StoreProduct> findBySlugAndDeletedAtIsNull(String slug);

    Optional<StoreProduct> findByIdAndDeletedAtIsNull(Long id);

    Page<StoreProduct> findByStatusAndDeletedAtIsNull(StoreProductStatus status, Pageable pageable);

    Page<StoreProduct> findByStatusAndIsActiveTrueAndDeletedAtIsNull(StoreProductStatus status, Pageable pageable);

    @Query(
        "SELECT p FROM StoreProduct p WHERE p.status = :status AND p.isActive = true AND p.deletedAt IS NULL AND " +
        "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
        "(:brandId IS NULL OR p.brand.id = :brandId) AND " +
        "(:minPrice IS NULL OR COALESCE(p.discountedPrice, p.price) >= :minPrice) AND " +
        "(:maxPrice IS NULL OR COALESCE(p.discountedPrice, p.price) <= :maxPrice) AND " +
        "(:featured IS NULL OR p.isFeatured = :featured)"
    )
    Page<StoreProduct> findActiveByFilters(
        @Param("status") StoreProductStatus status,
        @Param("categoryId") Long categoryId,
        @Param("brandId") Long brandId,
        @Param("minPrice") java.math.BigDecimal minPrice,
        @Param("maxPrice") java.math.BigDecimal maxPrice,
        @Param("featured") Boolean featured,
        Pageable pageable
    );

    @Query(
        "SELECT p FROM StoreProduct p WHERE p.status = 'ACTIVE' AND p.isActive = true AND p.deletedAt IS NULL AND " +
        "(LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
        "LOWER(p.category.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.brand.name) LIKE LOWER(CONCAT('%', :query, '%')))"
    )
    Page<StoreProduct> searchActive(@Param("query") String query, Pageable pageable);

    @Query(
        "SELECT p FROM StoreProduct p WHERE p.status = 'ACTIVE' AND p.isActive = true AND p.deletedAt IS NULL AND p.discountedPrice IS NOT NULL AND p.discountedPrice > 0"
    )
    Page<StoreProduct> findDiscounted(Pageable pageable);

    @Query(
        "SELECT p FROM StoreProduct p WHERE p.status = 'ACTIVE' AND p.isActive = true AND p.deletedAt IS NULL ORDER BY p.salesCount DESC"
    )
    Page<StoreProduct> findBestSellers(Pageable pageable);

    boolean existsBySkuAndDeletedAtIsNull(String sku);
}
