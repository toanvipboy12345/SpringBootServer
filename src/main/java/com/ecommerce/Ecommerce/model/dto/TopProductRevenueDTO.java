package com.ecommerce.Ecommerce.model.dto;

public class TopProductRevenueDTO {
    private Long productId;
    private String name;
    private Double revenue;

    // Constructor
    public TopProductRevenueDTO(Long productId, String name, Double revenue) {
        this.productId = productId;
        this.name = name;
        this.revenue = revenue;
    }

    // Getters and Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getRevenue() {
        return revenue;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }
}