// src/main/java/com/ecommerce/Ecommerce/repository/NotificationRepository.java
package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Lấy tất cả thông báo, sắp xếp theo thời gian tạo (mới nhất trước)
    List<Notification> findAllByOrderByCreatedAtDesc();

    // Xóa các thông báo cũ hơn 30 ngày
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.createdAt < :threshold")
    void deleteOlderThan(LocalDateTime threshold);
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.createdAt >= :start AND n.createdAt < :end")
    long countByCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}