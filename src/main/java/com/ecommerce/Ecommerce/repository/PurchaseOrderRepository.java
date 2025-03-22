package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    Optional<PurchaseOrder> findByPurchaseOrderCode(String purchaseOrderCode);
    boolean existsByPurchaseOrderCode(String purchaseOrderCode);

    @Modifying
    @Query("DELETE FROM PurchaseOrder po WHERE po.product.id = :productId")
    void deleteByProductId(Long productId);
    long count();
    @Query("SELECT po.supplier.id, po.supplier.name, SUM(po.totalAmount) " +
           "FROM PurchaseOrder po " +
           "WHERE po.status = 'COMPLETED' " +
           "AND po.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY po.supplier.id, po.supplier.name")
    List<Object[]> getTotalTransactionAmountBySupplier(@Param("startDate") LocalDateTime startDate, 
                                                       @Param("endDate") LocalDateTime endDate);
   
}