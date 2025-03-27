package com.ecommerce.Ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore // Ẩn toàn bộ object user khỏi JSON
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore // Ẩn toàn bộ object product khỏi JSON
    private Product product;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @ElementCollection
    @CollectionTable(name = "feedback_images", joinColumns = @JoinColumn(name = "feedback_id"))
    @Column(name = "image_path")
    private List<String> images = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // No-args constructor
    public Feedback() {
        this.images = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
    }

    // All-args constructor
    public Feedback(User user, Product product, Integer rating, String comment, List<String> images) {
        this.user = user;
        this.product = product;
        this.rating = rating;
        this.comment = comment;
        this.images = images != null ? images : new ArrayList<>();
        this.createdAt = LocalDateTime.now();
    }

    // Getters và Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @JsonProperty("userId") // Trả về userId trong JSON
    public Long getUserId() { return user.getId(); }

    @JsonProperty("productId") // Trả về productId trong JSON
    public Long getProductId() { return product.getId(); }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}