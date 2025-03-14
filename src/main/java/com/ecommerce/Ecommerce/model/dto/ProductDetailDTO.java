package com.ecommerce.Ecommerce.model.dto;

import java.util.List;

public class ProductDetailDTO {
    private Long id; // ID của sản phẩm
    private String code; // Mã sản phẩm
    private String name; // Tên sản phẩm
    private Double price; // Giá bán
    private Double discountPrice; // Giá giảm
    private Integer discountRate; // Tỷ lệ giảm giá
    private String description; // Mô tả
    private String brandName; // Tên thương hiệu
    private String categoryName; // Tên danh mục
    private List<ProductVariantDetail> variants; // Danh sách biến thể chi tiết

    // Inner class để chứa thông tin biến thể
    public static class ProductVariantDetail {
        private Long variantId; // ID của biến thể
        private String color; // Màu sắc
        private String mainImage; // Ảnh chính
        private List<String> images; // Danh sách ảnh phụ
        private List<VariantSizeDetail> sizes; // Danh sách kích thước

        // Inner class để chứa thông tin kích thước
        public static class VariantSizeDetail {
            private Long sizeId; // ID của kích thước
            private String size; // Thay Size enum bằng String
            private Integer quantity; // Số lượng

            public VariantSizeDetail() {}

            public VariantSizeDetail(Long sizeId, String size, Integer quantity) {
                this.sizeId = sizeId;
                this.size = size;
                this.quantity = quantity;
            }

            // Getters and Setters
            public Long getSizeId() {
                return sizeId;
            }

            public void setSizeId(Long sizeId) {
                this.sizeId = sizeId;
            }

            public String getSize() { // Cập nhật getter
                return size;
            }

            public void setSize(String size) { // Cập nhật setter
                this.size = size;
            }

            public Integer getQuantity() {
                return quantity;
            }

            public void setQuantity(Integer quantity) {
                this.quantity = quantity;
            }
        }

        public ProductVariantDetail() {}

        public ProductVariantDetail(Long variantId, String color, String mainImage, List<String> images, List<VariantSizeDetail> sizes) {
            this.variantId = variantId;
            this.color = color;
            this.mainImage = mainImage;
            this.images = images;
            this.sizes = sizes;
        }

        // Getters and Setters (giữ nguyên)
        public Long getVariantId() {
            return variantId;
        }

        public void setVariantId(Long variantId) {
            this.variantId = variantId;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getMainImage() {
            return mainImage;
        }

        public void setMainImage(String mainImage) {
            this.mainImage = mainImage;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }

        public List<VariantSizeDetail> getSizes() {
            return sizes;
        }

        public void setSizes(List<VariantSizeDetail> sizes) {
            this.sizes = sizes;
        }
    }

    // Constructors
    public ProductDetailDTO() {}

    public ProductDetailDTO(Long id, String code, String name, Double price, Double discountPrice, Integer discountRate, 
                          String description, String brandName, String categoryName, List<ProductVariantDetail> variants) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.price = price;
        this.discountPrice = discountPrice;
        this.discountRate = discountRate;
        this.description = description;
        this.brandName = brandName;
        this.categoryName = categoryName;
        this.variants = variants;
    }

    // Getters and Setters (giữ nguyên)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(Double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public Integer getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(Integer discountRate) {
        this.discountRate = discountRate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<ProductVariantDetail> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariantDetail> variants) {
        this.variants = variants;
    }
}