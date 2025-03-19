package com.ecommerce.Ecommerce.model.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderDTO {

    private Long id;
    private String purchaseOrderCode;
    private String supplierName;
    private String productName;
    private String productVariantName; // Tên sản phẩm kết hợp với biến thể (ví dụ: "NEW ERA NEYYAN LOGO RS44 T-SHIRT - BLACK")
    private String mainImage; // Ảnh chính của biến thể (sẽ không dùng trong bảng, chỉ dùng trong modal nếu cần)
    private String status;
    private Double importPrice;
    private Double totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Danh sách mục nhập hàng
    private List<PurchaseOrderItem> items = new ArrayList<>();

    // Nested class để đại diện cho các mục nhập hàng
    public static class PurchaseOrderItem {
        private Long variantSizeId;
        private String size;
        private Integer quantity;
        private String variantColor; // Màu của biến thể
        private String variantMainImage; // Ảnh chính của biến thể

        // No-args constructor
        public PurchaseOrderItem() {}

        // All-args constructor
        public PurchaseOrderItem(Long variantSizeId, String size, Integer quantity, String variantColor, String variantMainImage) {
            this.variantSizeId = variantSizeId;
            this.size = size;
            this.quantity = quantity;
            this.variantColor = variantColor;
            this.variantMainImage = variantMainImage;
        }

        // Getters and Setters
        public Long getVariantSizeId() {
            return variantSizeId;
        }

        public void setVariantSizeId(Long variantSizeId) {
            this.variantSizeId = variantSizeId;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public String getVariantColor() {
            return variantColor;
        }

        public void setVariantColor(String variantColor) {
            this.variantColor = variantColor;
        }

        public String getVariantMainImage() {
            return variantMainImage;
        }

        public void setVariantMainImage(String variantMainImage) {
            this.variantMainImage = variantMainImage;
        }
    }

    // No-args constructor
    public PurchaseOrderDTO() {
        this.items = new ArrayList<>();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPurchaseOrderCode() {
        return purchaseOrderCode;
    }

    public void setPurchaseOrderCode(String purchaseOrderCode) {
        this.purchaseOrderCode = purchaseOrderCode;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductVariantName() {
        return productVariantName;
    }

    public void setProductVariantName(String productVariantName) {
        this.productVariantName = productVariantName;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<PurchaseOrderItem> getItems() {
        return items;
    }

    public void setItems(List<PurchaseOrderItem> items) {
        this.items = items;
    }

    public Double getImportPrice() {
        return importPrice;
    }

    public void setImportPrice(Double importPrice) {
        this.importPrice = importPrice;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}