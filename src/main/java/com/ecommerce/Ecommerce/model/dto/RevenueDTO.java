package com.ecommerce.Ecommerce.model.dto;

import java.util.List;

public class RevenueDTO {
    private String label; // Nhãn (ví dụ: "2025-03", "Brand X", "COD")
    private double value; // Giá trị doanh thu
    private List<RevenueDetailDTO> details; // Chi tiết (nếu cần)

    // Constructors
    public RevenueDTO() {}

    public RevenueDTO(String label, double value) {
        this.label = label;
        this.value = value;
    }

    public RevenueDTO(String label, double value, List<RevenueDetailDTO> details) {
        this.label = label;
        this.value = value;
        this.details = details;
    }

    // Getters and Setters
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
    public List<RevenueDetailDTO> getDetails() { return details; }
    public void setDetails(List<RevenueDetailDTO> details) { this.details = details; }
}