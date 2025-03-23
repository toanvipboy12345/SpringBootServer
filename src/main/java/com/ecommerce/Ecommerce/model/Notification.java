// src/main/java/com/ecommerce/Ecommerce/model/Notification.java
package com.ecommerce.Ecommerce.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String message;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_read")
    private boolean isRead;

    // Constructors
    public Notification() {
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }

    public Notification(String message) {
        this.message = message;
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        this.isRead = read;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}