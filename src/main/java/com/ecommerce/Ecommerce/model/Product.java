package com.ecommerce.Ecommerce.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "product")
public class Product extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // Mã sản phẩm chính (ví dụ: "NE-TS-001")

    @Column(name = "brand_id", nullable = false)
    private Long brandId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(nullable = false)
    private String name; // Tên chung của sản phẩm (ví dụ: "NEW ERA NY CHAIN STITCH T-SHIRT")

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ProductVariant> variants = new ArrayList<>();

    @Column(name = "import_price", nullable = false)
    private Double importPrice; // Giá nhập hàng

    @Column(nullable = false)
    private Double price; // Giá gốc chung

    @Column(name = "discount_price", nullable = true)
    private Double discountPrice; // Giá giảm chung (nếu có)

    @Column(name = "discount_rate", nullable = true)
    private Integer discountRate; // Tỷ lệ giảm giá chung (nếu có)

    @Column(columnDefinition = "TEXT")
    private String description; // Mô tả chung

    // No-args constructor
    public Product() {
        this.variants = new ArrayList<>();
    }

    // All-args constructor
    public Product(Long id, String code, Long brandId, Long categoryId, String name, List<ProductVariant> variants,
            Double importPrice, Double price, Double discountPrice, Integer discountRate, String description) {
        this.id = id;
        this.code = code;
        this.brandId = brandId;
        this.categoryId = categoryId;
        this.name = name;
        this.variants = variants != null ? variants : new ArrayList<>();
        this.importPrice = importPrice;
        this.price = price;
        this.discountPrice = discountPrice;
        this.discountRate = discountRate;
        this.description = description;
    }

    // Getters và Setters
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

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProductVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariant> variants) {
        this.variants = variants;
    }

    public Double getImportPrice() {
        return importPrice;
    }

    public void setImportPrice(Double importPrice) {
        this.importPrice = importPrice;
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
}