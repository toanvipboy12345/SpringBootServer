package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.model.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    boolean existsByProductIdAndColor(Long productId, String color);
    boolean existsByProductIdAndColorAndIdNot(Long productId, String color, Long idNot);

    Page<ProductVariant> findAll(Specification<ProductVariant> spec, Pageable pageable);
    
    List<ProductVariant> findAll(Specification<ProductVariant> spec);
}