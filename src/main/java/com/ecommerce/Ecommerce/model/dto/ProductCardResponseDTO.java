package com.ecommerce.Ecommerce.model.dto;

import java.util.List;

public class ProductCardResponseDTO {
    private List<ProductCardDTO> products;
    private long total;

    // Constructors
    public ProductCardResponseDTO() {}

    public ProductCardResponseDTO(List<ProductCardDTO> products, long total) {
        this.products = products;
        this.total = total;
    }

    // Getters and Setters
    public List<ProductCardDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductCardDTO> products) {
        this.products = products;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}