package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    void deleteBySizeId(Long id);
}