package com.ecommerce.Ecommerce.service;

import com.ecommerce.Ecommerce.model.Wishlist;
import com.ecommerce.Ecommerce.model.ProductVariant;
import com.ecommerce.Ecommerce.model.dto.WishlistDTO;
import com.ecommerce.Ecommerce.repository.WishlistRepository;
import com.ecommerce.Ecommerce.repository.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    // Thêm sản phẩm vào wishlist
    public WishlistDTO addWishlist(Long userId, String wishlistToken, Long variantId) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        Wishlist wishlist;

        if (userId != null) {
            wishlist = wishlistRepository.findByUserIdAndVariantId(userId, variantId)
                    .orElseGet(() -> {
                        Wishlist newWishlist = new Wishlist(userId, null, variantId);
                        return wishlistRepository.save(newWishlist);
                    });
        } else {
            if (wishlistToken == null || wishlistToken.isEmpty()) {
                wishlistToken = UUID.randomUUID().toString();
            }
            String finalWishlistToken = wishlistToken;
            wishlist = wishlistRepository.findByWishlistTokenAndVariantId(wishlistToken, variantId)
                    .orElseGet(() -> {
                        Wishlist newWishlist = new Wishlist(null, finalWishlistToken, variantId);
                        return wishlistRepository.save(newWishlist);
                    });
        }

        return new WishlistDTO(
                wishlist.getId(),
                wishlist.getUserId(),
                wishlist.getWishlistToken(),
                variantId,
                variant.getProduct().getName(),
                variant.getColor(),
                variant.getMainImage(),
                variant.getProduct().getPrice(),
                variant.getProduct().getDiscountPrice()
        );
    }

    // Lấy danh sách wishlist
    public List<WishlistDTO> getWishlist(Long userId, String wishlistToken) {
        List<Wishlist> wishlistItems;

        // Nếu có userId, lấy wishlist theo userId
        if (userId != null) {
            wishlistItems = wishlistRepository.findByUserId(userId);
        } 
        // Nếu không có userId, lấy wishlist theo wishlistToken
        else if (wishlistToken != null && !wishlistToken.isEmpty()) {
            wishlistItems = wishlistRepository.findByWishlistToken(wishlistToken);
        } 
        // Nếu cả hai đều null, trả về danh sách rỗng
        else {
            return List.of();
        }

        // Chuyển đổi sang DTO
        return wishlistItems.stream().map(wishlist -> {
            ProductVariant variant = productVariantRepository.findById(wishlist.getVariantId())
                    .orElseThrow(() -> new RuntimeException("Variant not found"));
            return new WishlistDTO(
                    wishlist.getId(),
                    wishlist.getUserId(),
                    wishlist.getWishlistToken(),
                    wishlist.getVariantId(),
                    variant.getProduct().getName(),
                    variant.getColor(),
                    variant.getMainImage(),
                    variant.getProduct().getPrice(),
                    variant.getProduct().getDiscountPrice()
            );
        }).collect(Collectors.toList());
    }

    // Xóa một sản phẩm khỏi wishlist
    public void deleteWishlistItem(Long id) {
        Wishlist wishlist = wishlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wishlist item not found"));
        wishlistRepository.delete(wishlist);
    }
}