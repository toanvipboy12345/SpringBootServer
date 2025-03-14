package com.ecommerce.Ecommerce.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "variant_size")
public class VariantSize {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "variant_id", nullable = false)
    @JsonBackReference
    private ProductVariant variant;

    @Column(nullable = false, length = 10) // Giữ độ dài để khớp với varchar(10) hiện tại
    private String size;

    @Column(nullable = false)
    private Integer quantity;

    // No-args constructor
    public VariantSize() {}

    // All-args constructor
    public VariantSize(ProductVariant variant, String size, Integer quantity) {
        this.variant = variant;
        this.size = size;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ProductVariant getVariant() { return variant; }
    public void setVariant(ProductVariant variant) { this.variant = variant; }
    public String getSize() { return size; } // Cập nhật getter
    public void setSize(String size) { this.size = size; } // Cập nhật setter
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}