// src/main/java/com/ecommerce/Ecommerce/service/NotificationService.java
package com.ecommerce.Ecommerce.service;

import com.ecommerce.Ecommerce.model.Notification;
import com.ecommerce.Ecommerce.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    // Tạo thông báo mới
    public void createNotification(String message) {
        Notification notification = new Notification(message);
        notificationRepository.save(notification);
    }

    // Lấy tất cả thông báo
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAllByOrderByCreatedAtDesc();
    }

    // Xóa thông báo cũ hơn 30 ngày
    @Transactional
    public void deleteOldNotifications() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        notificationRepository.deleteOlderThan(thirtyDaysAgo);
    }

    // Đánh dấu thông báo là đã đọc (nếu cần)
    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}