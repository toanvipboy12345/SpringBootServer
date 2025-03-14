package com.ecommerce.Ecommerce.controller;

import com.ecommerce.Ecommerce.model.dto.ProductCardDTO;
import com.ecommerce.Ecommerce.model.dto.ProductCardResponseDTO;
import com.ecommerce.Ecommerce.model.dto.ProductDetailDTO;
import com.ecommerce.Ecommerce.model.Brand;
import com.ecommerce.Ecommerce.model.Category;
import com.ecommerce.Ecommerce.model.Product;
import com.ecommerce.Ecommerce.model.ProductVariant;
import com.ecommerce.Ecommerce.repository.BrandRepository;
import com.ecommerce.Ecommerce.repository.CategoryRepository;
import com.ecommerce.Ecommerce.repository.ProductVariantRepository;
import com.ecommerce.Ecommerce.service.ProductDtoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/product-dto")
public class ProductDtoController {

    @Autowired
    private ProductDtoService productDtoService;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;


    @GetMapping("/cards")
    public ResponseEntity<ProductCardResponseDTO> getProductCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Double priceMin,
            @RequestParam(required = false) Double priceMax,
            @RequestParam(required = false, defaultValue = "") String sort) { // Mặc định sort là rỗng

        Pageable pageable = PageRequest.of(page, size);

        Specification<ProductVariant> spec = new Specification<ProductVariant>() {
            @Override
            public Predicate toPredicate(Root<ProductVariant> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                Join<ProductVariant, Product> productJoin = root.join("product");

                if (category != null && !category.isEmpty()) {
                    Category cat = categoryRepository.findByName(category)
                            .orElseThrow(() -> new RuntimeException("Category not found"));
                    predicates.add(cb.equal(productJoin.get("categoryId"), cat.getId()));
                }

                if (brand != null && !brand.isEmpty()) {
                    Brand br = brandRepository.findByName(brand)
                            .orElseThrow(() -> new RuntimeException("Brand not found"));
                    predicates.add(cb.equal(productJoin.get("brandId"), br.getId()));
                }

                if (priceMin != null) {
                    predicates.add(cb.greaterThanOrEqualTo(productJoin.get("price"), priceMin));
                }
                if (priceMax != null) {
                    predicates.add(cb.lessThanOrEqualTo(productJoin.get("price"), priceMax));
                }

                // Thêm sắp xếp vào query
                if ("price_asc".equals(sort)) {
                    query.orderBy(cb.asc(productJoin.get("price")));
                } else if ("price_desc".equals(sort)) {
                    query.orderBy(cb.desc(productJoin.get("price")));
                } else if ("created_at_asc".equals(sort)) { // Sắp xếp theo thời gian tạo của Product (tăng dần)
                    query.orderBy(cb.asc(productJoin.get("createdAt")));
                } else if ("created_at_desc".equals(sort)) { // Sắp xếp theo thời gian tạo của Product (giảm dần)
                    query.orderBy(cb.desc(productJoin.get("createdAt")));
                }

                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };

        Page<ProductVariant> variants = productVariantRepository.findAll(spec, pageable);

        List<ProductCardDTO> dtos = productDtoService.getProductCards(page, size, category, brand, priceMin, priceMax,
                sort);

        ProductCardResponseDTO response = new ProductCardResponseDTO(dtos, variants.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<ProductDetailDTO> getProductDetail(
            @PathVariable Long id,
            @RequestParam(required = false) Long variantId) {
        ProductDetailDTO dto = productDtoService.getProductDetail(id, variantId);
        return ResponseEntity.ok(dto);
    }
    @GetMapping("/search")
    public ResponseEntity<List<ProductCardDTO>> searchProductCards(
            @RequestParam String keyword) {
        List<ProductCardDTO> dtos = productDtoService.searchProductCards(keyword);
        return ResponseEntity.ok(dtos);
    }
    
}