package com.ecommerce.Ecommerce.controller;

import com.ecommerce.Ecommerce.model.dto.CartDTO;
import com.ecommerce.Ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<CartDTO> addToCart(
            @RequestHeader(value = "Cart-Token", required = false) String cartToken,
            @RequestBody CartRequestBody requestBody,
            @RequestHeader(value = "User-Id", required = false) Long userId) {
        try {
            System.out.println("Received User-Id from header: " + userId);
            CartDTO response = cartService.addToCart(cartToken, userId, 
                    requestBody.getProductId(), requestBody.getVariantId(), requestBody.getSizes());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Error in addToCart: " + e.getMessage());
            e.printStackTrace(System.out); // In chi tiết lỗi ra console
           return ResponseEntity.status(500).body(new CartDTO(null, null, null, null, 0, 0.0));
        }
    }
    @GetMapping
    public ResponseEntity<CartDTO> getCart(
            @RequestHeader(value = "Cart-Token", required = false) String cartToken,
            @RequestHeader(value = "User-Id", required = false) Long userId) {
        if (userId != null) {
            CartDTO response = cartService.getCart(null, userId);
            return ResponseEntity.ok(response);
        } else if (cartToken != null && !cartToken.isEmpty()) {
            CartDTO response = cartService.getCart(cartToken, null);
            return ResponseEntity.ok(response);
        } else {
            throw new IllegalArgumentException("Either User-Id or Cart-Token is required");
        }
    }

    @GetMapping("/guest")
    public ResponseEntity<CartDTO> getGuestCart() {
        CartDTO response = cartService.getGuestCart();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/merge")
    public ResponseEntity<CartDTO> mergeCartOnLogin(
            @RequestHeader(value = "Cart-Token", required = false) String cartToken,
            @RequestHeader(value = "User-Id", required = true) Long userId) {
        try {
            CartDTO response = cartService.mergeCartOnLogin(cartToken, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
           return ResponseEntity.status(500).body(new CartDTO(null, null, null, null, 0, 0.0));
        }
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<CartDTO> removeItemFromCart(
            @RequestHeader(value = "Cart-Token", required = false) String cartToken,
            @RequestHeader(value = "User-Id", required = false) Long userId,
            @PathVariable Long itemId) {
        try {
            if (userId != null) {
                CartDTO response = cartService.removeItemFromCart(null, userId, itemId);
                return ResponseEntity.ok(response);
            } else if (cartToken != null && !cartToken.isEmpty()) {
                CartDTO response = cartService.removeItemFromCart(cartToken, null, itemId);
                return ResponseEntity.ok(response);
            } else {
                throw new IllegalArgumentException("Either User-Id or Cart-Token is required");
            }
        } catch (Exception e) {
           return ResponseEntity.status(500).body(new CartDTO(null, null, null, null, 0, 0.0));
        }
    }

    @PutMapping("/item/{itemId}")
    public ResponseEntity<CartDTO> updateItemQuantity(
            @RequestHeader(value = "Cart-Token", required = false) String cartToken,
            @RequestHeader(value = "User-Id", required = false) Long userId,
            @PathVariable Long itemId,
            @RequestBody UpdateQuantityRequest requestBody) {
        try {
            if (userId != null) {
                CartDTO response = cartService.updateItemQuantity(null, userId, itemId, requestBody.getQuantity());
                return ResponseEntity.ok(response);
            } else if (cartToken != null && !cartToken.isEmpty()) {
                CartDTO response = cartService.updateItemQuantity(cartToken, null, itemId, requestBody.getQuantity());
                return ResponseEntity.ok(response);
            } else {
                throw new IllegalArgumentException("Either User-Id or Cart-Token is required");
            }
        } catch (Exception e) {
           return ResponseEntity.status(500).body(new CartDTO(null, null, null, null, 0, 0.0));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CartDTO> getCartByUserId(@PathVariable Long userId) {
        CartDTO response = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(response);
    }

    public static class CartRequestBody {
        private Long productId;
        private Long variantId;
        private List<CartDTO.SizeQuantityRequest> sizes;

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public Long getVariantId() { return variantId; }
        public void setVariantId(Long variantId) { this.variantId = variantId; }
        public List<CartDTO.SizeQuantityRequest> getSizes() { return sizes; }
        public void setSizes(List<CartDTO.SizeQuantityRequest> sizes) { this.sizes = sizes; }
    }

    public static class UpdateQuantityRequest {
        private Integer quantity;

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}