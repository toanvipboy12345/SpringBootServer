// package com.ecommerce.Ecommerce.repository;

// import com.ecommerce.Ecommerce.model.Order;
// import org.springframework.data.jpa.repository.JpaRepository;

// import java.util.List;
// import java.util.Optional;

// public interface OrderRepository extends JpaRepository<Order, Long> {
//     Optional<Order> findByOrderId(String orderId);
//     List<Order> findByUserId(Long userId);
//     List<Order> findByCartToken(String cartToken); // Thêm phương thức tìm theo cartToken
//     long count();
    
// }
package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.model.Order;
import com.ecommerce.Ecommerce.model.OrderStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderId(String orderId);
    List<Order> findByUserId(Long userId);
    List<Order> findByCartToken(String cartToken);
    long count();

    // Lấy danh sách đơn hàng có trạng thái DELIVERED trong khoảng thời gian
    List<Order> findByStatusAndCreatedAtBetween(OrderStatus status, LocalDateTime startDate, LocalDateTime endDate);

    // Thống kê doanh thu theo thời gian (group by ngày, tuần, tháng, năm) - Sử dụng native query
    @Query(value = "SELECT " +
           "CASE " +
           "  WHEN :groupBy = 'DAY' THEN DATE_FORMAT(o.created_at, '%Y-%m-%d') " +
           "  WHEN :groupBy = 'WEEK' THEN DATE_FORMAT(o.created_at, '%Y-%u') " +
           "  WHEN :groupBy = 'MONTH' THEN DATE_FORMAT(o.created_at, '%Y-%m') " +
           "  WHEN :groupBy = 'YEAR' THEN DATE_FORMAT(o.created_at, '%Y') " +
           "END as timeUnit, " +
           "SUM(o.total_amount) as total " +
           "FROM orders o " +
           "WHERE o.status = 'DELIVERED' AND o.created_at BETWEEN :startDate AND :endDate " +
           "GROUP BY " +
           "CASE " +
           "  WHEN :groupBy = 'DAY' THEN DATE_FORMAT(o.created_at, '%Y-%m-%d') " +
           "  WHEN :groupBy = 'WEEK' THEN DATE_FORMAT(o.created_at, '%Y-%u') " +
           "  WHEN :groupBy = 'MONTH' THEN DATE_FORMAT(o.created_at, '%Y-%m') " +
           "  WHEN :groupBy = 'YEAR' THEN DATE_FORMAT(o.created_at, '%Y') " +
           "END " +
           "ORDER BY timeUnit", nativeQuery = true)
    List<Object[]> findRevenueByTime(@Param("groupBy") String groupBy, 
                                     @Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);

    // Thống kê doanh thu theo thương hiệu
    @Query("SELECT p.brandId, SUM(oi.price * oi.quantity) as total, p.id as productId, p.name as productName, SUM(oi.quantity) as quantitySold " +
           "FROM Order o " +
           "JOIN o.items oi " +
           "JOIN oi.product p " +
           "WHERE o.status = 'DELIVERED' AND o.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY p.brandId, p.id, p.name")
    List<Object[]> findRevenueByBrand(LocalDateTime startDate, LocalDateTime endDate);

    // Thống kê doanh thu theo phương thức thanh toán
    @Query("SELECT p.paymentMethod, SUM(o.totalAmount) as total " +
           "FROM Order o " +
           "JOIN o.payment p " +
           "WHERE o.status = 'DELIVERED' AND o.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY p.paymentMethod")
    List<Object[]> findRevenueByPaymentMethod(LocalDateTime startDate, LocalDateTime endDate);
}