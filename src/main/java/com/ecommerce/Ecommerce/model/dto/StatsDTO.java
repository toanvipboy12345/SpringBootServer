package com.ecommerce.Ecommerce.model.dto;

public class StatsDTO {
    private long totalProducts;          // Tổng số sản phẩm
    private long totalVariants;          // Tổng số biến thể
    private long totalUsers;             // Tổng số user
    private long totalRegularUsers;      // Tổng số user có role = "user"
    private long totalAdmins;            // Tổng số user có role = "admin"
    private long totalOrders;            // Tổng số đơn hàng
    private long totalPurchaseOrders;    // Tổng số phiếu nhập hàng
    private long totalCategories;        // Tổng số danh mục
    private long totalBrands;            // Tổng số thương hiệu
    private long totalSuppliers;         // Tổng số nhà cung cấp
    private long totalShippingMethods;   // Tổng số phương thức vận chuyển
    private long totalCoupons;           // Tổng số mã giảm giá

    // Constructor
    public StatsDTO(
            long totalProducts,
            long totalVariants,
            long totalUsers,
            long totalRegularUsers,
            long totalAdmins,
            long totalOrders,
            long totalPurchaseOrders,
            long totalCategories,
            long totalBrands,
            long totalSuppliers,
            long totalShippingMethods,
            long totalCoupons) {
        this.totalProducts = totalProducts;
        this.totalVariants = totalVariants;
        this.totalUsers = totalUsers;
        this.totalRegularUsers = totalRegularUsers;
        this.totalAdmins = totalAdmins;
        this.totalOrders = totalOrders;
        this.totalPurchaseOrders = totalPurchaseOrders;
        this.totalCategories = totalCategories;
        this.totalBrands = totalBrands;
        this.totalSuppliers = totalSuppliers;
        this.totalShippingMethods = totalShippingMethods;
        this.totalCoupons = totalCoupons;
    }

    // Getters và Setters
    public long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public long getTotalVariants() {
        return totalVariants;
    }

    public void setTotalVariants(long totalVariants) {
        this.totalVariants = totalVariants;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalRegularUsers() {
        return totalRegularUsers;
    }

    public void setTotalRegularUsers(long totalRegularUsers) {
        this.totalRegularUsers = totalRegularUsers;
    }

    public long getTotalAdmins() {
        return totalAdmins;
    }

    public void setTotalAdmins(long totalAdmins) {
        this.totalAdmins = totalAdmins;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public long getTotalPurchaseOrders() {
        return totalPurchaseOrders;
    }

    public void setTotalPurchaseOrders(long totalPurchaseOrders) {
        this.totalPurchaseOrders = totalPurchaseOrders;
    }

    public long getTotalCategories() {
        return totalCategories;
    }

    public void setTotalCategories(long totalCategories) {
        this.totalCategories = totalCategories;
    }

    public long getTotalBrands() {
        return totalBrands;
    }

    public void setTotalBrands(long totalBrands) {
        this.totalBrands = totalBrands;
    }

    public long getTotalSuppliers() {
        return totalSuppliers;
    }

    public void setTotalSuppliers(long totalSuppliers) {
        this.totalSuppliers = totalSuppliers;
    }

    public long getTotalShippingMethods() {
        return totalShippingMethods;
    }

    public void setTotalShippingMethods(long totalShippingMethods) {
        this.totalShippingMethods = totalShippingMethods;
    }

    public long getTotalCoupons() {
        return totalCoupons;
    }

    public void setTotalCoupons(long totalCoupons) {
        this.totalCoupons = totalCoupons;
    }
}