package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.model.ProductVariant;
import com.ecommerce.Ecommerce.model.VariantSize;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariantSizeRepository extends JpaRepository<VariantSize, Long> {
    // Không cần decreaseQuantity nếu chỉ kiểm tra tồn kho
    Optional<VariantSize> findByVariantAndSize(ProductVariant variant, String size);
    long count();
}