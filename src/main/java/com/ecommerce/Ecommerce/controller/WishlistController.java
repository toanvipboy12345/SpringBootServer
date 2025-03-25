package com.ecommerce.Ecommerce.controller;

import com.ecommerce.Ecommerce.model.dto.WishlistDTO;
import com.ecommerce.Ecommerce.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    // Endpoint thêm sản phẩm vào wishlist
    @PostMapping("/add")
    public ResponseEntity<WishlistDTO> addWishlist(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String wishlistToken,
            @RequestParam Long variantId) {
        WishlistDTO wishlistDTO = wishlistService.addWishlist(userId, wishlistToken, variantId);
        return ResponseEntity.ok(wishlistDTO);
    }

    // Endpoint lấy danh sách wishlist
    @GetMapping
    public ResponseEntity<List<WishlistDTO>> getWishlist(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String wishlistToken) {
        List<WishlistDTO> wishlist = wishlistService.getWishlist(userId, wishlistToken);
        return ResponseEntity.ok(wishlist);
    }
}