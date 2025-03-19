package com.ecommerce.Ecommerce.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class PurchaseOrderItem {

    @Column(name = "variant_size_id", nullable = false)
    private Long variantSizeId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    // No-args constructor
    public PurchaseOrderItem() {}

    // All-args constructor
    public PurchaseOrderItem(Long variantSizeId, Integer quantity) {
        this.variantSizeId = variantSizeId;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Long getVariantSizeId() {
        return variantSizeId;
    }

    public void setVariantSizeId(Long variantSizeId) {
        this.variantSizeId = variantSizeId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}