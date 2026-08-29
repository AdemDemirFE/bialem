package com.bialem.backend.store.web.rest;

import com.bialem.backend.domain.Profile;
import com.bialem.backend.service.AppSupport;
import com.bialem.backend.store.service.StoreCatalogService;
import com.bialem.backend.store.service.dto.StoreBrandDTO;
import com.bialem.backend.store.service.dto.StoreCategoryDTO;
import com.bialem.backend.store.service.dto.StoreProductDTO;
import com.bialem.backend.store.service.dto.StoreProductListDTO;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/store")
public class StoreCatalogResource {

    private final StoreCatalogService catalogService;
    private final AppSupport appSupport;

    public StoreCatalogResource(StoreCatalogService catalogService, AppSupport appSupport) {
        this.catalogService = catalogService;
        this.appSupport = appSupport;
    }

    @GetMapping("/categories")
    public ResponseEntity<List<StoreCategoryDTO>> getCategories() {
        return ResponseEntity.ok(catalogService.getActiveCategories());
    }

    @GetMapping("/categories/tree")
    public ResponseEntity<List<StoreCategoryDTO>> getCategoryTree() {
        return ResponseEntity.ok(catalogService.getCategoryTree());
    }

    @GetMapping("/categories/{slug}")
    public ResponseEntity<StoreCategoryDTO> getCategory(@PathVariable String slug) {
        return ResponseEntity.ok(catalogService.getCategoryBySlug(slug));
    }

    @GetMapping("/brands")
    public ResponseEntity<List<StoreBrandDTO>> getBrands() {
        return ResponseEntity.ok(catalogService.getActiveBrands());
    }

    @GetMapping("/products")
    public ResponseEntity<Page<StoreProductListDTO>> listProducts(
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) Long brandId,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice,
        @RequestParam(required = false) String sort,
        Pageable pageable
    ) {
        return ResponseEntity.ok(catalogService.filterProducts(categoryId, brandId, minPrice, maxPrice, sort, pageable));
    }

    @GetMapping("/products/search")
    public ResponseEntity<Page<StoreProductListDTO>> searchProducts(@RequestParam String query, Pageable pageable) {
        return ResponseEntity.ok(catalogService.searchProducts(query, pageable));
    }

    @GetMapping("/products/featured")
    public ResponseEntity<List<StoreProductListDTO>> getFeaturedProducts(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(catalogService.getFeaturedProducts(limit));
    }

    @GetMapping("/products/new")
    public ResponseEntity<List<StoreProductListDTO>> getNewProducts(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(catalogService.getNewProducts(limit));
    }

    @GetMapping("/products/bestsellers")
    public ResponseEntity<List<StoreProductListDTO>> getBestSellers(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(catalogService.getBestSellers(limit));
    }

    @GetMapping("/products/discounted")
    public ResponseEntity<List<StoreProductListDTO>> getDiscountedProducts(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(catalogService.getDiscountedProducts(limit));
    }

    @GetMapping("/products/{slug}")
    public ResponseEntity<StoreProductDTO> getProduct(@PathVariable String slug) {
        return ResponseEntity.ok(catalogService.getProductBySlug(slug));
    }

    // Admin catalog management
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN','ROLE_STORE_MANAGER','ROLE_STORE_ADMIN')")
    @PostMapping("/admin/categories")
    public ResponseEntity<StoreCategoryDTO> createCategory(@Valid @RequestBody StoreCategoryDTO dto) {
        Profile profile = appSupport.currentProfile();
        return ResponseEntity.ok(catalogService.saveCategory(dto, profile.getDisplayName()));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN','ROLE_STORE_MANAGER','ROLE_STORE_ADMIN')")
    @PutMapping("/admin/categories/{id}")
    public ResponseEntity<StoreCategoryDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody StoreCategoryDTO dto) {
        dto.setId(id);
        Profile profile = appSupport.currentProfile();
        return ResponseEntity.ok(catalogService.saveCategory(dto, profile.getDisplayName()));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN','ROLE_STORE_MANAGER','ROLE_STORE_ADMIN')")
    @PostMapping("/admin/brands")
    public ResponseEntity<StoreBrandDTO> createBrand(@Valid @RequestBody StoreBrandDTO dto) {
        Profile profile = appSupport.currentProfile();
        return ResponseEntity.ok(catalogService.saveBrand(dto, profile.getDisplayName()));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN','ROLE_STORE_MANAGER','ROLE_STORE_ADMIN')")
    @PutMapping("/admin/brands/{id}")
    public ResponseEntity<StoreBrandDTO> updateBrand(@PathVariable Long id, @Valid @RequestBody StoreBrandDTO dto) {
        dto.setId(id);
        Profile profile = appSupport.currentProfile();
        return ResponseEntity.ok(catalogService.saveBrand(dto, profile.getDisplayName()));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN','ROLE_STORE_MANAGER','ROLE_STORE_ADMIN')")
    @PostMapping("/admin/products")
    public ResponseEntity<StoreProductDTO> createProduct(@Valid @RequestBody StoreProductDTO dto) {
        Profile profile = appSupport.currentProfile();
        return ResponseEntity.ok(catalogService.saveProduct(dto, profile.getDisplayName()));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN','ROLE_STORE_MANAGER','ROLE_STORE_ADMIN')")
    @PutMapping("/admin/products/{id}")
    public ResponseEntity<StoreProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody StoreProductDTO dto) {
        dto.setId(id);
        Profile profile = appSupport.currentProfile();
        return ResponseEntity.ok(catalogService.saveProduct(dto, profile.getDisplayName()));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN','ROLE_STORE_MANAGER','ROLE_STORE_ADMIN')")
    @DeleteMapping("/admin/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        Profile profile = appSupport.currentProfile();
        catalogService.softDeleteProduct(id, profile.getDisplayName());
        return ResponseEntity.noContent().build();
    }
}
