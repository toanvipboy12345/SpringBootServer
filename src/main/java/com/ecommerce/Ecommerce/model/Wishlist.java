package com.ecommerce.Ecommerce.model;

import jakarta.persistence.*;

@Entity
@Table(name = "wishlist")
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = true)
    private Long userId; // Null nếu chưa đăng nhập

    @Column(name = "wishlist_token", nullable = true)
    private String wishlistToken; // Token cho khách chưa đăng nhập

    @Column(name = "variant_id", nullable = false)
    private Long variantId; // ID của biến thể sản phẩm

    // No-args constructor
    public Wishlist() {}

    // All-args constructor
    public Wishlist(Long userId, String wishlistToken, Long variantId) {
        this.userId = userId;
        this.wishlistToken = wishlistToken;
        this.variantId = variantId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getWishlistToken() {
        return wishlistToken;
    }

    public void setWishlistToken(String wishlistToken) {
        this.wishlistToken = wishlistToken;
    }

    public Long getVariantId() {
        return variantId;
    }

    public void setVariantId(Long variantId) {
        this.variantId = variantId;
    }
}