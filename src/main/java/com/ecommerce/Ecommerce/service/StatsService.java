package com.ecommerce.Ecommerce.service;

import com.ecommerce.Ecommerce.model.dto.StatsDTO;
import com.ecommerce.Ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class StatsService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final SupplierRepository supplierRepository;
    private final ShippingMethodRepository shippingMethodRepository;
    private final CouponRepository couponRepository;
    private final NotificationRepository notificationRepository; // Thêm NotificationRepository

    @Autowired
    public StatsService(
            ProductRepository productRepository,
            ProductVariantRepository productVariantRepository,
            UserRepository userRepository,
            OrderRepository orderRepository,
            PurchaseOrderRepository purchaseOrderRepository,
            CategoryRepository categoryRepository,
            BrandRepository brandRepository,
            SupplierRepository supplierRepository,
            ShippingMethodRepository shippingMethodRepository,
            CouponRepository couponRepository,
            NotificationRepository notificationRepository) {
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.supplierRepository = supplierRepository;
        this.shippingMethodRepository = shippingMethodRepository;
        this.couponRepository = couponRepository;
        this.notificationRepository = notificationRepository;
    }

    public StatsDTO getStats() {
        // Lấy ngày hiện tại
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        // Các thống kê hiện có
        long totalProducts = productRepository != null ? productRepository.count() : 0;
        long totalVariants = productVariantRepository != null ? productVariantRepository.count() : 0;
        long totalUsers = userRepository != null ? userRepository.count() : 0;
        long totalRegularUsers = userRepository != null ? userRepository.countByRoleUser() : 0;
        long totalAdmins = userRepository != null ? userRepository.countByRoleAdmin() : 0;
        long totalOrders = orderRepository != null ? orderRepository.count() : 0;
        long totalPurchaseOrders = purchaseOrderRepository != null ? purchaseOrderRepository.count() : 0;
        long totalCategories = categoryRepository != null ? categoryRepository.count() : 0;
        long totalBrands = brandRepository != null ? brandRepository.count() : 0;
        long totalSuppliers = supplierRepository != null ? supplierRepository.count() : 0;
        long totalShippingMethods = shippingMethodRepository != null ? shippingMethodRepository.count() : 0;
        long totalCoupons = couponRepository != null ? couponRepository.count() : 0;

        // Thống kê mới: Số lượng thông báo và đơn hàng trong ngày hiện tại
        long totalNotificationsToday = notificationRepository != null
                ? notificationRepository.countByCreatedAtBetween(startOfDay, endOfDay)
                : 0;
        long totalOrdersToday = orderRepository != null
                ? orderRepository.countByCreatedAtBetween(startOfDay, endOfDay)
                : 0;

        return new StatsDTO(
                totalProducts,
                totalVariants,
                totalUsers,
                totalRegularUsers,
                totalAdmins,
                totalOrders,
                totalPurchaseOrders,
                totalCategories,
                totalBrands,
                totalSuppliers,
                totalShippingMethods,
                totalCoupons,
                totalNotificationsToday,
                totalOrdersToday
        );
    }
}