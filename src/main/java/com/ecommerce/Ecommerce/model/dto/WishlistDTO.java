package com.ecommerce.Ecommerce.model.dto;

public class WishlistDTO {
    private Long id;
    private Long userId; // Null nếu chưa đăng nhập
    private String wishlistToken; // Token cho khách chưa đăng nhập
    private Long variantId;
    private String productName;
    private String color;
    private String mainImage;
    private Double price;
    private Double discountPrice;

    // Constructor
    public WishlistDTO(Long id, Long userId, String wishlistToken, Long variantId, String productName, 
                       String color, String mainImage, Double price, Double discountPrice) {
        this.id = id;
        this.userId = userId;
        this.wishlistToken = wishlistToken;
        this.variantId = variantId;
        this.productName = productName;
        this.color = color;
        this.mainImage = mainImage;
        this.price = price;
        this.discountPrice = discountPrice;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getWishlistToken() { return wishlistToken; }
    public void setWishlistToken(String wishlistToken) { this.wishlistToken = wishlistToken; }
    public Long getVariantId() { return variantId; }
    public void setVariantId(Long variantId) { this.variantId = variantId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getMainImage() { return mainImage; }
    public void setMainImage(String mainImage) { this.mainImage = mainImage; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Double getDiscountPrice() { return discountPrice; }
    public void setDiscountPrice(Double discountPrice) { this.discountPrice = discountPrice; }
}