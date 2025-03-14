package com.ecommerce.Ecommerce.model;

public enum OrderStatus {
    PENDING,    // Đơn hàng đang chờ xử lý
    CONFIRMED,  // Đơn hàng đã được xác nhận (sau khi thanh toán thành công)
    SHIPPED,    // Đơn hàng đã được gửi đi
    DELIVERED,  // Đơn hàng đã giao thành công
    CANCELLED   // Đơn hàng bị hủy
}