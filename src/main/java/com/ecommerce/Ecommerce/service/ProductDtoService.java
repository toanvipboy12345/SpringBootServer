package com.ecommerce.Ecommerce.service;

import com.ecommerce.Ecommerce.model.dto.ProductCardDTO;
import com.ecommerce.Ecommerce.model.dto.ProductDetailDTO;
import com.ecommerce.Ecommerce.model.*;
import com.ecommerce.Ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Join;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductDtoService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    public List<ProductCardDTO> getProductCards(int page, int size, String category, String brand, Double priceMin, Double priceMax, String sort) {
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

        // Chuyển đổi sang ProductCardDTO, bao gồm createdAt từ Product
        return variants.stream()
                .map(variant -> {
                    Product product = variant.getProduct();
                    // Chuyển đổi LocalDateTime của createdAt từ Product (Auditable) thành Long (timestamp)
                    Long createdAtTimestamp = product.getCreatedAt() != null 
                        ? product.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() // Sử dụng atZone và toEpochMilli
                        : null;
                    return new ProductCardDTO(
                            product.getId(),
                            variant.getId(),
                            product.getCode(),
                            product.getName() + " - " + variant.getColor(),
                            product.getPrice(),
                            product.getDiscountPrice(),
                            product.getDiscountRate(),
                            variant.getMainImage(),
                            createdAtTimestamp // Sử dụng createdAt từ Product
                    );
                })
                .collect(Collectors.toList());
    }
    

    // Phương thức getProductDetail không thay đổi (nếu không cần thêm createdAt)
    public ProductDetailDTO getProductDetail(Long productId, Long variantId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Brand brand = brandRepository.findById(product.getBrandId())
                .orElseThrow(() -> new RuntimeException("Brand not found"));
        Category category = categoryRepository.findById(product.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        List<ProductDetailDTO.ProductVariantDetail> variantDetails;
        if (variantId != null) {
            ProductVariant selectedVariant = product.getVariants().stream()
                    .filter(variant -> variant.getId().equals(variantId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Variant not found"));
            variantDetails = List.of(new ProductDetailDTO.ProductVariantDetail(
                    selectedVariant.getId(),
                    selectedVariant.getColor(),
                    selectedVariant.getMainImage(),
                    selectedVariant.getImages(),
                    selectedVariant.getSizes().stream()
                            .map(size -> new ProductDetailDTO.ProductVariantDetail.VariantSizeDetail(
                                    size.getId(),
                                    size.getSize(),
                                    size.getQuantity()
                            )).collect(Collectors.toList())
            ));
        } else {
            variantDetails = product.getVariants().stream()
                    .map(variant -> new ProductDetailDTO.ProductVariantDetail(
                            variant.getId(),
                            variant.getColor(),
                            variant.getMainImage(),
                            variant.getImages(),
                            variant.getSizes().stream()
                                    .map(size -> new ProductDetailDTO.ProductVariantDetail.VariantSizeDetail(
                                            size.getId(),
                                            size.getSize(),
                                            size.getQuantity()
                                    )).collect(Collectors.toList())
                    )).collect(Collectors.toList());
        }

        return new ProductDetailDTO(
                product.getId(),
                product.getCode(),
                product.getName(),
                product.getPrice(),
                product.getDiscountPrice(),
                product.getDiscountRate(),
                product.getDescription(),
                brand.getName(),
                category.getName(),
                variantDetails
        );
    }
    public List<ProductCardDTO> searchProductCards(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>(); // Trả về danh sách rỗng nếu không có từ khóa
        }

        Specification<ProductVariant> spec = new Specification<ProductVariant>() {
            @Override
            public Predicate toPredicate(Root<ProductVariant> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                Join<ProductVariant, Product> productJoin = root.join("product");

                // Tìm kiếm trong tên sản phẩm hoặc mã sản phẩm (không phân biệt hoa thường)
                String likePattern = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(productJoin.get("name")), likePattern),
                        cb.like(cb.lower(productJoin.get("code")), likePattern)
                ));

                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };

        List<ProductVariant> variants = productVariantRepository.findAll(spec);

        // Chuyển đổi sang ProductCardDTO
        return variants.stream()
                .map(variant -> {
                    Product product = variant.getProduct();
                    Long createdAtTimestamp = product.getCreatedAt() != null
                            ? product.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                            : null;
                    return new ProductCardDTO(
                            product.getId(),
                            variant.getId(),
                            product.getCode(),
                            product.getName() + " - " + variant.getColor(),
                            product.getPrice(),
                            product.getDiscountPrice(),
                            product.getDiscountRate(),
                            variant.getMainImage(),
                            createdAtTimestamp
                    );
                })
                .collect(Collectors.toList());
    }
    
}