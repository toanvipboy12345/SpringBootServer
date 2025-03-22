package com.ecommerce.Ecommerce.model.dto;

public class SupplierTransactionStatsDTO {

    private Long supplierId;
    private String supplierName;
    private Double totalTransactionAmount;

    // No-args constructor
    public SupplierTransactionStatsDTO() {}

    // All-args constructor
    public SupplierTransactionStatsDTO(Long supplierId, String supplierName, Double totalTransactionAmount) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.totalTransactionAmount = totalTransactionAmount;
    }

    // Getters and Setters
    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Double getTotalTransactionAmount() {
        return totalTransactionAmount;
    }

    public void setTotalTransactionAmount(Double totalTransactionAmount) {
        this.totalTransactionAmount = totalTransactionAmount;
    }
}