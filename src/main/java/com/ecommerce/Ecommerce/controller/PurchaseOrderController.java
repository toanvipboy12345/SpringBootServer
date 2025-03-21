package com.ecommerce.Ecommerce.controller;

import com.ecommerce.Ecommerce.annotation.RequireAdminRole;
import com.ecommerce.Ecommerce.model.PurchaseOrder;
import com.ecommerce.Ecommerce.model.dto.PurchaseOrderDTO;
import com.ecommerce.Ecommerce.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @Autowired
    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    // Lấy danh sách tất cả phiếu nhập hàng
    @GetMapping
    public ResponseEntity<List<PurchaseOrderDTO>> getAllPurchaseOrders(
            @RequestParam(value = "search", required = false) String search) {
        List<PurchaseOrderDTO> purchaseOrders = purchaseOrderService.getAllPurchaseOrders(search);
        return ResponseEntity.ok(purchaseOrders);
    }

    // Lấy phiếu nhập hàng theo ID
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderDTO> getPurchaseOrderById(@PathVariable Long id) {
        Optional<PurchaseOrderDTO> purchaseOrder = purchaseOrderService.getPurchaseOrderById(id);
        return purchaseOrder.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Tạo phiếu nhập hàng mới
    @PostMapping
    @RequireAdminRole(roles = {"super_admin", "product_manager"})
    public ResponseEntity<PurchaseOrder> createPurchaseOrder(@RequestBody PurchaseOrder purchaseOrder) {
        try {
            PurchaseOrder createdPurchaseOrder = purchaseOrderService.createPurchaseOrder(purchaseOrder);
            return ResponseEntity.status(201).body(createdPurchaseOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Xác nhận phiếu nhập hàng
    @PatchMapping("/{id}/confirm")
    @RequireAdminRole(roles = {"super_admin", "product_manager"})
    public ResponseEntity<PurchaseOrder> confirmPurchaseOrder(@PathVariable Long id) {
        try {
            PurchaseOrder confirmedPurchaseOrder = purchaseOrderService.confirmPurchaseOrder(id);
            return ResponseEntity.ok(confirmedPurchaseOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Hủy phiếu nhập hàng
    @PatchMapping("/{id}/cancel")
    @RequireAdminRole(roles = {"super_admin", "product_manager"})
    public ResponseEntity<PurchaseOrder> cancelPurchaseOrder(@PathVariable Long id) {
        try {
            PurchaseOrder cancelledPurchaseOrder = purchaseOrderService.cancelPurchaseOrder(id);
            return ResponseEntity.ok(cancelledPurchaseOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}