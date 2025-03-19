package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    Optional<PurchaseOrder> findByPurchaseOrderCode(String purchaseOrderCode);
    boolean existsByPurchaseOrderCode(String purchaseOrderCode);

    @Modifying
    @Query("DELETE FROM PurchaseOrder po WHERE po.product.id = :productId")
    void deleteByProductId(Long productId);
}