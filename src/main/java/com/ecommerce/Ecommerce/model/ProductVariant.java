package com.ecommerce.Ecommerce.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * Entity representing a variant of a product, including color, images, and
 * associated sizes.
 */
@Entity
@Table(name = "product_variant")
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "variant_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference
    private Product product;
    @Column(nullable = false)
    private String color; // Màu sắc của biến thể (ví dụ: "Red", "Black")

    @Column(name = "main_image", nullable = true)
    private String mainImage; // Ảnh chính của biến thể

    @ElementCollection
    @CollectionTable(name = "variant_images", joinColumns = @JoinColumn(name = "variant_id"))
    @Column(name = "image_path")
    private List<String> images = new ArrayList<>(); // Danh sách ảnh phụ của biến thể

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VariantSize> sizes = new ArrayList<>(); // Danh sách kích thước và số lượng cho biến thể

    // No-args constructor
    public ProductVariant() {
        this.images = new ArrayList<>();
        this.sizes = new ArrayList<>();
    }

    // All-args constructor
    public ProductVariant(Product product, String color) {
        this.product = product;
        this.color = color;
        this.images = new ArrayList<>();
        this.sizes = new ArrayList<>();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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

    public List<VariantSize> getSizes() {
        return sizes;
    }

    public void setSizes(List<VariantSize> sizes) {
        this.sizes = sizes;
    }
}