// src/main/java/com/ecommerce/Ecommerce/controller/NotificationController.java
package com.ecommerce.Ecommerce.controller;

import com.ecommerce.Ecommerce.model.Notification;
import com.ecommerce.Ecommerce.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // Tạo thông báo mới
    @PostMapping
    public void createNotification(@RequestBody NotificationRequest request) {
        notificationService.createNotification(request.getMessage());
    }

    // Lấy tất cả thông báo
    @GetMapping
    public List<Notification> getAllNotifications() {
        // Xóa thông báo cũ hơn 30 ngày trước khi trả về danh sách
        notificationService.deleteOldNotifications();
        return notificationService.getAllNotifications();
    }

    // Đánh dấu thông báo là đã đọc
    @PutMapping("/{id}")
    public void markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
    }
}

// DTO cho request
class NotificationRequest {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}