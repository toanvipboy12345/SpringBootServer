package com.ecommerce.Ecommerce.model.dto;

import java.util.List;

public class CartDTO {
    private Long id;
    private String cartToken; // Dùng cho khách chưa đăng nhập
    private Long userId; // Thêm trường userId, null nếu chưa đăng nhập
    private List<CartItemDTO> items;
    private Integer totalItems;
    private Double totalPrice;
   

    // Nested DTO cho CartItem
    public static class CartItemDTO {
        private Long id;
        private Long productId;
        private String productName;
        private VariantDTO variant;
        private Long sizeId;
        private String size;
        private Integer quantity;
        private Integer availableQuantity; // Thêm số lượng tồn kho
        private Double price;
        private Double discountPrice;

        // Nested DTO cho Variant
        public static class VariantDTO {
            private Long id;
            private String color;
            private String mainImage;

            public VariantDTO(Long id, String color, String mainImage) {
                this.id = id;
                this.color = color;
                this.mainImage = mainImage;
            }

            // Getters
            public Long getId() { return id; }
            public String getColor() { return color; }
            public String getMainImage() { return mainImage; }
        }

        // Constructor
        public CartItemDTO(Long id, Long productId, String productName, VariantDTO variant, 
                          Long sizeId, String size, Integer quantity, Integer availableQuantity, 
                          Double price, Double discountPrice) {
            this.id = id;
            this.productId = productId;
            this.productName = productName;
            this.variant = variant;
            this.sizeId = sizeId;
            this.size = size;
            this.quantity = quantity;
            this.availableQuantity = availableQuantity;
            this.price = price;
            this.discountPrice = discountPrice;
        }

        // Getters
        public Long getId() { return id; }
        public Long getProductId() { return productId; }
        public String getProductName() { return productName; }
        public VariantDTO getVariant() { return variant; }
        public Long getSizeId() { return sizeId; }
        public String getSize() { return size; }
        public Integer getQuantity() { return quantity; }
        public Integer getAvailableQuantity() { return availableQuantity; }
        public Double getPrice() { return price; }
        public Double getDiscountPrice() { return discountPrice; }
    }

    // Nested DTO cho SizeQuantityRequest
    public static class SizeQuantityRequest {
        private Long sizeId;
        private Integer quantity;

        public SizeQuantityRequest() {}
        public SizeQuantityRequest(Long sizeId, Integer quantity) {
            this.sizeId = sizeId;
            this.quantity = quantity;
        }

        // Getters and Setters
        public Long getSizeId() { return sizeId; }
        public void setSizeId(Long sizeId) { this.sizeId = sizeId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    // Constructor
    public CartDTO(Long id, String cartToken, Long userId, List<CartItemDTO> items, 
                   Integer totalItems, Double totalPrice) {
        this.id = id;
        this.cartToken = cartToken;
        this.userId = userId;
        this.items = items;
        this.totalItems = totalItems;
        this.totalPrice = totalPrice;
       
    }

    // Getters
    public Long getId() { return id; }
    public String getCartToken() { return cartToken; }
    public Long getUserId() { return userId; }
    public List<CartItemDTO> getItems() { return items; }
    public Integer getTotalItems() { return totalItems; }
    public Double getTotalPrice() { return totalPrice; }
   
}