package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findByUserId(Long userId);
    List<Wishlist> findByWishlistToken(String wishlistToken);
    Optional<Wishlist> findByUserIdAndVariantId(Long userId, Long variantId);
    Optional<Wishlist> findByWishlistTokenAndVariantId(String wishlistToken, Long variantId);
}