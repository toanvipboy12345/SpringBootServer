package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.model.Cart;
import com.ecommerce.Ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // Tìm giỏ hàng theo User (dành cho khách đã đăng nhập)
    Optional<Cart> findByUser(User user);
    Optional<Cart> findByUserId(Long userId);
    // Tìm giỏ hàng theo cartToken (dành cho khách không đăng nhập)
    Optional<Cart> findByCartToken(String cartToken);

    // Kiểm tra xem cartToken đã tồn tại chưa
    boolean existsByCartToken(String cartToken);
}