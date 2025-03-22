package com.ecommerce.Ecommerce.model.dto;

public class RevenueDetailDTO {
    private String itemId; // ID của mục (ví dụ: productId)
    private String itemName; // Tên của mục (ví dụ: tên sản phẩm)
    private double revenue; // Doanh thu của mục
    private int quantitySold; // Số lượng đã bán

    // Constructors
    public RevenueDetailDTO() {}

    public RevenueDetailDTO(String itemId, String itemName, double revenue, int quantitySold) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.revenue = revenue;
        this.quantitySold = quantitySold;
    }

    // Getters and Setters
    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public double getRevenue() { return revenue; }
    public void setRevenue(double revenue) { this.revenue = revenue; }
    public int getQuantitySold() { return quantitySold; }
    public void setQuantitySold(int quantitySold) { this.quantitySold = quantitySold; }
}