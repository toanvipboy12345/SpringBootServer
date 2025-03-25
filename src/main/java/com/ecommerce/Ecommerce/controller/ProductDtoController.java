package com.ecommerce.Ecommerce.controller;

import com.ecommerce.Ecommerce.model.dto.ProductCardDTO;
import com.ecommerce.Ecommerce.model.dto.ProductCardResponseDTO;
import com.ecommerce.Ecommerce.model.dto.ProductDetailDTO;
import com.ecommerce.Ecommerce.model.*;
import com.ecommerce.Ecommerce.repository.*;
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
import java.util.Collections; // Thêm import này
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
            @RequestParam(required = false, defaultValue = "") String sort) {

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

                // Không áp dụng sắp xếp ở đây, để service xử lý theo logic hiện tại
                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };

        Page<ProductVariant> variants = productVariantRepository.findAll(spec, pageable);

        // Gọi service để lấy danh sách DTO (vẫn giữ logic sắp xếp trong service)
        List<ProductCardDTO> dtos = productDtoService.getProductCards(page, size, category, brand, priceMin, priceMax, sort);

        // Trộn ngẫu nhiên danh sách DTO
        Collections.shuffle(dtos);

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