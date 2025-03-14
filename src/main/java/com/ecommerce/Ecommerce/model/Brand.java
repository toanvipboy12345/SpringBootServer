package com.ecommerce.Ecommerce.model;

import jakarta.persistence.*;

@Entity
@Table(name = "brand")
public class Brand extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String description;

    @Column(name = "image_path", nullable = true)
    private String image;

    // No-args constructor
    public Brand() {}

    // Constructor with id
    public Brand(Long id) {
        this.id = id;
    }

    // Getters và Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}