package com.ecommerce.Ecommerce.model.dto;

public class TopProductRevenueDTO {
    private Long productId;
    private String name;
    private Double revenue;
    private Integer quantitySold; // Thêm trường quantitySold

    // Constructor
    public TopProductRevenueDTO(Long productId, String name, Double revenue, Integer quantitySold) {
        this.productId = productId;
        this.name = name;
        this.revenue = revenue;
        this.quantitySold = quantitySold;
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

    public Integer getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(Integer quantitySold) {
        this.quantitySold = quantitySold;
    }
}