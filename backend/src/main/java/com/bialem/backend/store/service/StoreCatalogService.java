package com.bialem.backend.store.service;

import com.bialem.backend.store.domain.*;
import com.bialem.backend.store.domain.enumeration.StoreProductStatus;
import com.bialem.backend.store.repository.*;
import com.bialem.backend.store.service.dto.*;
import com.bialem.backend.store.service.mapper.StoreMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class StoreCatalogService {

    private static final Logger LOG = LoggerFactory.getLogger(StoreCatalogService.class);

    private final StoreCategoryRepository categoryRepository;
    private final StoreBrandRepository brandRepository;
    private final StoreProductRepository productRepository;
    private final StoreProductImageRepository productImageRepository;
    private final StoreProductVariantRepository productVariantRepository;
    private final StoreProductAttributeRepository productAttributeRepository;
    private final StoreMapper mapper;

    public StoreCatalogService(
        StoreCategoryRepository categoryRepository,
        StoreBrandRepository brandRepository,
        StoreProductRepository productRepository,
        StoreProductImageRepository productImageRepository,
        StoreProductVariantRepository productVariantRepository,
        StoreProductAttributeRepository productAttributeRepository,
        StoreMapper mapper
    ) {
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.productVariantRepository = productVariantRepository;
        this.productAttributeRepository = productAttributeRepository;
        this.mapper = mapper;
    }

    public List<StoreCategoryDTO> getActiveCategories() {
        return categoryRepository.findByIsActiveTrueAndDeletedAtIsNullOrderBySortOrderAsc().stream().map(mapper::toCategoryDTO).toList();
    }

    public List<StoreCategoryDTO> getCategoryTree() {
        return categoryRepository.findByParentIsNullAndDeletedAtIsNullOrderBySortOrderAsc().stream().map(mapper::toCategoryTree).toList();
    }

    public StoreCategoryDTO getCategoryBySlug(String slug) {
        return categoryRepository
            .findBySlugAndDeletedAtIsNull(slug)
            .map(mapper::toCategoryDTO)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kategori bulunamadı"));
    }

    public List<StoreBrandDTO> getActiveBrands() {
        return brandRepository.findByIsActiveTrueAndDeletedAtIsNullOrderByNameAsc().stream().map(mapper::toBrandDTO).toList();
    }

    public StoreBrandDTO getBrandBySlug(String slug) {
        return brandRepository
            .findBySlugAndDeletedAtIsNull(slug)
            .map(mapper::toBrandDTO)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Marka bulunamadı"));
    }

    public Page<StoreProductListDTO> listActiveProducts(Pageable pageable) {
        return productRepository.findByStatusAndIsActiveTrueAndDeletedAtIsNull(StoreProductStatus.ACTIVE, pageable).map(mapper::toProductListDTO);
    }

    public Page<StoreProductListDTO> listActiveProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository
            .findActiveByFilters(StoreProductStatus.ACTIVE, categoryId, null, null, null, null, pageable)
            .map(mapper::toProductListDTO);
    }

    public Page<StoreProductListDTO> searchProducts(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            return listActiveProducts(pageable);
        }
        return productRepository.searchActive(query.trim(), pageable).map(mapper::toProductListDTO);
    }

    public Page<StoreProductListDTO> filterProducts(
        Long categoryId,
        Long brandId,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String sort,
        Pageable pageable
    ) {
        Pageable sorted = applySort(pageable, sort);
        return productRepository
            .findActiveByFilters(StoreProductStatus.ACTIVE, categoryId, brandId, minPrice, maxPrice, null, sorted)
            .map(mapper::toProductListDTO);
    }

    public StoreProductDTO getProductBySlug(String slug) {
        StoreProduct product = productRepository
            .findBySlugAndDeletedAtIsNull(slug)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ürün bulunamadı"));
        return mapper.toProductDTO(product);
    }

    public StoreProductDTO getProductById(Long id) {
        StoreProduct product = productRepository
            .findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ürün bulunamadı"));
        return mapper.toProductDTO(product);
    }

    public List<StoreProductListDTO> getFeaturedProducts(int limit) {
        return productRepository
            .findActiveByFilters(StoreProductStatus.ACTIVE, null, null, null, null, true, PageRequest.of(0, limit))
            .map(mapper::toProductListDTO)
            .getContent();
    }

    public List<StoreProductListDTO> getNewProducts(int limit) {
        return productRepository
            .findByStatusAndIsActiveTrueAndDeletedAtIsNull(StoreProductStatus.ACTIVE, PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt")))
            .map(mapper::toProductListDTO)
            .getContent();
    }

    public List<StoreProductListDTO> getBestSellers(int limit) {
        return productRepository.findBestSellers(PageRequest.of(0, limit)).map(mapper::toProductListDTO).getContent();
    }

    public List<StoreProductListDTO> getDiscountedProducts(int limit) {
        return productRepository.findDiscounted(PageRequest.of(0, limit)).map(mapper::toProductListDTO).getContent();
    }

    private Pageable applySort(Pageable pageable, String sort) {
        Sort s = switch (sort == null ? "" : sort) {
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "newest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "bestseller" -> Sort.by(Sort.Direction.DESC, "salesCount");
            case "rating" -> Sort.by(Sort.Direction.DESC, "ratingAverage");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), s);
    }

    @Transactional
    public StoreCategoryDTO saveCategory(StoreCategoryDTO dto, String changedBy) {
        StoreCategory category;
        if (dto.getId() != null) {
            category = categoryRepository
                .findById(dto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kategori bulunamadı"));
            category.setUpdatedAt(Instant.now());
            category.setUpdatedBy(changedBy);
        } else {
            category = new StoreCategory();
            category.setCreatedAt(Instant.now());
            category.setCreatedBy(changedBy);
        }
        mapper.updateCategoryFromDTO(category, dto);
        if (dto.getParentId() != null) {
            StoreCategory parent = categoryRepository
                .findById(dto.getParentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Üst kategori bulunamadı"));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }
        return mapper.toCategoryDTO(categoryRepository.save(category));
    }

    @Transactional
    public StoreBrandDTO saveBrand(StoreBrandDTO dto, String changedBy) {
        StoreBrand brand;
        if (dto.getId() != null) {
            brand = brandRepository.findById(dto.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Marka bulunamadı"));
            brand.setUpdatedAt(Instant.now());
            brand.setUpdatedBy(changedBy);
        } else {
            brand = new StoreBrand();
            brand.setCreatedAt(Instant.now());
            brand.setCreatedBy(changedBy);
        }
        mapper.updateBrandFromDTO(brand, dto);
        return mapper.toBrandDTO(brandRepository.save(brand));
    }

    @Transactional
    public StoreProductDTO saveProduct(StoreProductDTO dto, String changedBy) {
        StoreProduct product;
        if (dto.getId() != null) {
            product = productRepository
                .findByIdAndDeletedAtIsNull(dto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ürün bulunamadı"));
            product.setUpdatedAt(Instant.now());
            product.setUpdatedBy(changedBy);
        } else {
            product = new StoreProduct();
            product.setCreatedAt(Instant.now());
            product.setCreatedBy(changedBy);
        }
        mapper.updateProductFromDTO(product, dto);
        if (dto.getCategoryId() != null) {
            product.setCategory(
                categoryRepository.findById(dto.getCategoryId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kategori bulunamadı"))
            );
        }
        if (dto.getBrandId() != null) {
            product.setBrand(brandRepository.findById(dto.getBrandId()).orElse(null));
        }
        product = productRepository.save(product);
        return mapper.toProductDTO(product);
    }

    @Transactional
    public void softDeleteProduct(Long id, String changedBy) {
        StoreProduct product = productRepository
            .findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ürün bulunamadı"));
        product.setDeletedAt(Instant.now());
        product.setUpdatedAt(Instant.now());
        product.setUpdatedBy(changedBy);
    }
}
