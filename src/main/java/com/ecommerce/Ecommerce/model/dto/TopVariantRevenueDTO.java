package com.ecommerce.Ecommerce.model.dto;

public class TopVariantRevenueDTO {
    private Long productId;
    private Long variantId;
    private String name;
    private Double price;
    private Double discountPrice;
    private String mainImage;
    private Double revenue;
    private Integer quantitySold;

    // Constructor
    public TopVariantRevenueDTO(Long productId, Long variantId, String name, Double price, 
                                Double discountPrice, String mainImage, Double revenue, Integer quantitySold) {
        this.productId = productId;
        this.variantId = variantId;
        this.name = name;
        this.price = price;
        this.discountPrice = discountPrice;
        this.mainImage = mainImage;
        this.revenue = revenue;
        this.quantitySold = quantitySold;
    }

    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Long getVariantId() { return variantId; }
    public void setVariantId(Long variantId) { this.variantId = variantId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Double getDiscountPrice() { return discountPrice; }
    public void setDiscountPrice(Double discountPrice) { this.discountPrice = discountPrice; }
    public String getMainImage() { return mainImage; }
    public void setMainImage(String mainImage) { this.mainImage = mainImage; }
    public Double getRevenue() { return revenue; }
    public void setRevenue(Double revenue) { this.revenue = revenue; }
    public Integer getQuantitySold() { return quantitySold; }
    public void setQuantitySold(Integer quantitySold) { this.quantitySold = quantitySold; }
}