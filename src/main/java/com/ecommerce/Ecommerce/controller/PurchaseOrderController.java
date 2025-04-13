
// package com.ecommerce.Ecommerce.controller;

// import com.ecommerce.Ecommerce.annotation.RequireAdminRole;
// import com.ecommerce.Ecommerce.model.PurchaseOrder;
// import com.ecommerce.Ecommerce.model.dto.PurchaseOrderDTO;
// import com.ecommerce.Ecommerce.service.NotificationService;
// import com.ecommerce.Ecommerce.service.PurchaseOrderService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;
// import java.util.Optional;

// @RestController
// @RequestMapping("/api/purchase-orders")
// public class PurchaseOrderController {

//     private final PurchaseOrderService purchaseOrderService;
//     private final NotificationService notificationService; // Thêm NotificationService

//     @Autowired
//     public PurchaseOrderController(
//             PurchaseOrderService purchaseOrderService,
//             NotificationService notificationService) { // Inject NotificationService
//         this.purchaseOrderService = purchaseOrderService;
//         this.notificationService = notificationService;
//     }

//     // Lấy danh sách tất cả phiếu nhập hàng
//     @GetMapping
//     public ResponseEntity<List<PurchaseOrderDTO>> getAllPurchaseOrders(
//             @RequestParam(value = "search", required = false) String search) {
//         List<PurchaseOrderDTO> purchaseOrders = purchaseOrderService.getAllPurchaseOrders(search);
//         return ResponseEntity.ok(purchaseOrders);
//     }

//     // Lấy phiếu nhập hàng theo ID
//     @GetMapping("/{id}")
//     public ResponseEntity<PurchaseOrderDTO> getPurchaseOrderById(@PathVariable Long id) {
//         Optional<PurchaseOrderDTO> purchaseOrder = purchaseOrderService.getPurchaseOrderById(id);
//         return purchaseOrder.map(ResponseEntity::ok)
//                 .orElse(ResponseEntity.notFound().build());
//     }

//     // Tạo phiếu nhập hàng mới
//     @PostMapping
//     @RequireAdminRole(roles = {"super_admin", "product_manager"})
//     public ResponseEntity<PurchaseOrder> createPurchaseOrder(@RequestBody PurchaseOrder purchaseOrder) {
//         try {
//             PurchaseOrder createdPurchaseOrder = purchaseOrderService.createPurchaseOrder(purchaseOrder);
//             // Gửi thông báo với mã phiếu nhập hàng
//             notificationService.createNotification(
//                     "Phiếu nhập hàng " + createdPurchaseOrder.getPurchaseOrderCode() + " đã được tạo"
//             );
//             return ResponseEntity.status(201).body(createdPurchaseOrder);
//         } catch (IllegalArgumentException e) {
//             return ResponseEntity.badRequest().body(null);
//         }
//     }

//     // Xác nhận phiếu nhập hàng
//     @PatchMapping("/{id}/confirm")
//     @RequireAdminRole(roles = {"super_admin", "product_manager"})
//     public ResponseEntity<PurchaseOrder> confirmPurchaseOrder(@PathVariable Long id) {
//         try {
//             PurchaseOrder confirmedPurchaseOrder = purchaseOrderService.confirmPurchaseOrder(id);
//             // Gửi thông báo với mã phiếu nhập hàng
//             notificationService.createNotification(
//                     "Phiếu nhập hàng " + confirmedPurchaseOrder.getPurchaseOrderCode() + " đã được xác nhận"
//             );
//             return ResponseEntity.ok(confirmedPurchaseOrder);
//         } catch (IllegalArgumentException e) {
//             return ResponseEntity.badRequest().body(null);
//         }
//     }

//     // Hủy phiếu nhập hàng
//     @PatchMapping("/{id}/cancel")
//     @RequireAdminRole(roles = {"super_admin", "product_manager"})
//     public ResponseEntity<PurchaseOrder> cancelPurchaseOrder(@PathVariable Long id) {
//         try {
//             PurchaseOrder cancelledPurchaseOrder = purchaseOrderService.cancelPurchaseOrder(id);
//             // Gửi thông báo với mã phiếu nhập hàng
//             notificationService.createNotification(
//                     "Phiếu nhập hàng " + cancelledPurchaseOrder.getPurchaseOrderCode() + " đã bị hủy"
//             );
//             return ResponseEntity.ok(cancelledPurchaseOrder);
//         } catch (IllegalArgumentException e) {
//             return ResponseEntity.badRequest().body(null);
//         }
//     }
// }
package com.ecommerce.Ecommerce.controller;

import com.ecommerce.Ecommerce.annotation.RequireAdminRole;
import com.ecommerce.Ecommerce.model.PurchaseOrder;
import com.ecommerce.Ecommerce.model.dto.PurchaseOrderDTO;
import com.ecommerce.Ecommerce.service.NotificationService;
import com.ecommerce.Ecommerce.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;
    private final NotificationService notificationService;

    @Autowired
    public PurchaseOrderController(
            PurchaseOrderService purchaseOrderService,
            NotificationService notificationService) {
        this.purchaseOrderService = purchaseOrderService;
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<PurchaseOrderDTO>> getAllPurchaseOrders(
            @RequestParam(value = "search", required = false) String search) {
        List<PurchaseOrderDTO> purchaseOrders = purchaseOrderService.getAllPurchaseOrders(search);
        return ResponseEntity.ok(purchaseOrders);
    }

    @GetMapping("/{id}")
    @RequireAdminRole

    public ResponseEntity<PurchaseOrderDTO> getPurchaseOrderById(@PathVariable Long id) {
        Optional<PurchaseOrderDTO> purchaseOrder = purchaseOrderService.getPurchaseOrderById(id);
        return purchaseOrder.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @RequireAdminRole(roles = {"super_admin", "product_manager"})
    public ResponseEntity<PurchaseOrder> createPurchaseOrder(@RequestBody PurchaseOrder purchaseOrder) {
        try {
            PurchaseOrder createdPurchaseOrder = purchaseOrderService.createPurchaseOrder(purchaseOrder);
            notificationService.createNotification(
                    "Phiếu nhập hàng " + createdPurchaseOrder.getPurchaseOrderCode() + " đã được tạo"
            );
            return ResponseEntity.status(201).body(createdPurchaseOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PatchMapping("/{id}/confirm")
    @RequireAdminRole(roles = {"super_admin", "product_manager"})
    public ResponseEntity<PurchaseOrder> confirmPurchaseOrder(@PathVariable Long id) {
        try {
            PurchaseOrder confirmedPurchaseOrder = purchaseOrderService.confirmPurchaseOrder(id);
            notificationService.createNotification(
                    "Phiếu nhập hàng " + confirmedPurchaseOrder.getPurchaseOrderCode() + " đã được xác nhận"
            );
            return ResponseEntity.ok(confirmedPurchaseOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PatchMapping("/{id}/cancel")
    @RequireAdminRole(roles = {"super_admin", "product_manager"})
    public ResponseEntity<PurchaseOrder> cancelPurchaseOrder(@PathVariable Long id) {
        try {
            PurchaseOrder cancelledPurchaseOrder = purchaseOrderService.cancelPurchaseOrder(id);
            notificationService.createNotification(
                    "Phiếu nhập hàng " + cancelledPurchaseOrder.getPurchaseOrderCode() + " đã bị hủy"
            );
            return ResponseEntity.ok(cancelledPurchaseOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Endpoint xuất PDF
    @GetMapping("/{id}/export-pdf")
    @RequireAdminRole(roles = { "super_admin", "product_manager", "order_manager", "blog_manager","marketing_manager" })

    public ResponseEntity<byte[]> exportPurchaseOrderToPdf(@PathVariable Long id) {
        try {
            byte[] pdfBytes = purchaseOrderService.exportPurchaseOrderToPdf(id);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "purchase_order_" + id + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}