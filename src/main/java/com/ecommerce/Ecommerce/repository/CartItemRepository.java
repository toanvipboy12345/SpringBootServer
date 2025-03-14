package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.model.Cart;
import com.ecommerce.Ecommerce.model.CartItem;
import com.ecommerce.Ecommerce.model.ProductVariant;
import com.ecommerce.Ecommerce.model.VariantSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Tìm CartItem theo Cart, ProductVariant và VariantSize
    Optional<CartItem> findByCartAndVariantAndSize(Cart cart, ProductVariant variant, VariantSize size);
    List<CartItem> findByCart(Cart cart);
    // Xóa tất cả CartItem thuộc một Cart (nếu cần khi hợp nhất hoặc xóa giỏ)
    void deleteByCart(Cart cart);
}