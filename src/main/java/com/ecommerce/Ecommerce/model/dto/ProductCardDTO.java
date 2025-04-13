// package com.ecommerce.Ecommerce.model.dto;

// public class ProductCardDTO {
//     private Long productId; // Đổi từ id thành productId
//     private Long variantId;
//     private String code;
//     private String name;
//     private Double price;
//     private Double discountPrice;
//     private Integer discountRate;
//     private String mainImage;
//     private Long createdAt; // Thêm trường timestamp cho thời gian tạo của Product

//     // Constructors
//     public ProductCardDTO() {}

//     public ProductCardDTO(Long productId, Long variantId, String code, String name, Double price, Double discountPrice, Integer discountRate, String mainImage, Long createdAt) {
//         this.productId = productId;
//         this.variantId = variantId;
//         this.code = code;
//         this.name = name;
//         this.price = price;
//         this.discountPrice = discountPrice;
//         this.discountRate = discountRate;
//         this.mainImage = mainImage;
//         this.createdAt = createdAt; // Thêm createdAt vào constructor
//     }

//     // Getters and Setters
//     public Long getProductId() { return productId; }
//     public void setProductId(Long productId) { this.productId = productId; }
//     public Long getVariantId() { return variantId; }
//     public void setVariantId(Long variantId) { this.variantId = variantId; }
//     public String getCode() { return code; }
//     public void setCode(String code) { this.code = code; }
//     public String getName() { return name; }
//     public void setName(String name) { this.name = name; }
//     public Double getPrice() { return price; }
//     public void setPrice(Double price) { this.price = price; }
//     public Double getDiscountPrice() { return discountPrice; }
//     public void setDiscountPrice(Double discountPrice) { this.discountPrice = discountPrice; }
//     public Integer getDiscountRate() { return discountRate; }
//     public void setDiscountRate(Integer discountRate) { this.discountRate = discountRate; }
//     public String getMainImage() { return mainImage; }
//     public void setMainImage(String mainImage) { this.mainImage = mainImage; }
//     public Long getCreatedAt() { return createdAt; } // Thêm getter
//     public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; } // Thêm setter
// }
package com.ecommerce.Ecommerce.model.dto;

public class ProductCardDTO {
    private Long productId;
    private Long variantId;
    private String code;
    private String name;
    private Double price;
    private Double discountPrice;
    private Integer discountRate;
    private String mainImage;
    private Long createdAt;
    private Long quantitySold; // Thêm trường số lượng bán được

    // Constructors
    public ProductCardDTO() {}

    public ProductCardDTO(Long productId, Long variantId, String code, String name, Double price, Double discountPrice, 
                         Integer discountRate, String mainImage, Long createdAt, Long quantitySold) {
        this.productId = productId;
        this.variantId = variantId;
        this.code = code;
        this.name = name;
        this.price = price;
        this.discountPrice = discountPrice;
        this.discountRate = discountRate;
        this.mainImage = mainImage;
        this.createdAt = createdAt;
        this.quantitySold = quantitySold; // Thêm vào constructor
    }

    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Long getVariantId() { return variantId; }
    public void setVariantId(Long variantId) { this.variantId = variantId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Double getDiscountPrice() { return discountPrice; }
    public void setDiscountPrice(Double discountPrice) { this.discountPrice = discountPrice; }
    public Integer getDiscountRate() { return discountRate; }
    public void setDiscountRate(Integer discountRate) { this.discountRate = discountRate; }
    public String getMainImage() { return mainImage; }
    public void setMainImage(String mainImage) { this.mainImage = mainImage; }
    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
    public Long getQuantitySold() { return quantitySold; } // Thêm getter
    public void setQuantitySold(Long quantitySold) { this.quantitySold = quantitySold; } // Thêm setter
}