
// package com.ecommerce.Ecommerce.controller;

// import com.ecommerce.Ecommerce.model.Address;
// import com.ecommerce.Ecommerce.model.Order;
// import com.ecommerce.Ecommerce.model.OrderStatus;
// import com.ecommerce.Ecommerce.model.dto.CartDTO;
// import com.ecommerce.Ecommerce.model.dto.InvoiceDTO;
// import com.ecommerce.Ecommerce.service.OrderService;
// import com.ecommerce.Ecommerce.service.PaymentService; // Thêm import PaymentService
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;
// import java.util.Map;

// @RestController
// @RequestMapping("/api/orders")
// public class OrderController {

//     @Autowired
//     private OrderService orderService;

//     @Autowired
//     private PaymentService paymentService; // Thêm PaymentService

//     public static class OrderRequestDTO {
//         private String orderId;
//         private Long userId;
//         private CartDTO cartDTO;
//         private Address shippingAddress;
//         private String shippingMethodCode;
//         private String couponCode;
//         private String customerName;
//         private String phoneNumber;
//         private String email;

//         // Getters and Setters
//         public String getOrderId() { return orderId; }
//         public void setOrderId(String orderId) { this.orderId = orderId; }
//         public Long getUserId() { return userId; }
//         public void setUserId(Long userId) { this.userId = userId; }
//         public CartDTO getCartDTO() { return cartDTO; }
//         public void setCartDTO(CartDTO cartDTO) { this.cartDTO = cartDTO; }
//         public Address getShippingAddress() { return shippingAddress; }
//         public void setShippingAddress(Address shippingAddress) { this.shippingAddress = shippingAddress; }
//         public String getShippingMethodCode() { return shippingMethodCode; }
//         public void setShippingMethodCode(String shippingMethodCode) { this.shippingMethodCode = shippingMethodCode; }
//         public String getCouponCode() { return couponCode; }
//         public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
//         public String getEmail() { return email; }
//         public void setEmail(String email) { this.email = email; }
//         public String getCustomerName() { return customerName; }
//         public void setCustomerName(String customerName) { this.customerName = customerName; }
//         public String getPhoneNumber() { return phoneNumber; }
//         public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
//     }

//     public static class UpdateStatusRequestDTO {
//         private OrderStatus status;

//         // Getters and Setters
//         public OrderStatus getStatus() { return status; }
//         public void setStatus(OrderStatus status) { this.status = status; }
//     }

//     @GetMapping
//     public ResponseEntity<List<InvoiceDTO>> getAllOrders() {
//         List<InvoiceDTO> orders = orderService.getAllOrders();
//         return ResponseEntity.ok(orders);
//     }

//     @PostMapping("/create")
//     public ResponseEntity<Map<String, String>> createOrder(@RequestBody OrderRequestDTO request) {
//         Order order = orderService.createOrder(
//             request.getOrderId(),
//             request.getUserId(),
//             request.getCartDTO(),
//             request.getShippingAddress(),
//             request.getShippingMethodCode(),
//             request.getCouponCode(),
//             request.getEmail(),
//             request.getCustomerName(),
//             request.getPhoneNumber()
//         );
//         Map<String, String> response = Map.of("orderId", order.getOrderId());
//         return ResponseEntity.ok(response);
//     }

//     @GetMapping("/{orderId}")
//     public ResponseEntity<InvoiceDTO> getOrderByOrderId(@PathVariable String orderId) {
//         return orderService.getOrderByOrderId(orderId)
//                 .map(order -> ResponseEntity.ok(orderService.mapToInvoiceDTO(order)))
//                 .orElseGet(() -> ResponseEntity.notFound().build());
//     }

//     @GetMapping("/user/{userId}")
//     public ResponseEntity<List<InvoiceDTO>> getOrdersByUserId(@PathVariable Long userId) {
//         List<InvoiceDTO> invoiceDTOs = orderService.getOrdersByUserId(userId);
//         return ResponseEntity.ok(invoiceDTOs);
//     }

//     @GetMapping("/cart-token/{cartToken}")
//     public ResponseEntity<List<InvoiceDTO>> getOrdersByCartToken(@PathVariable String cartToken) {
//         List<InvoiceDTO> invoiceDTOs = orderService.getOrdersByCartToken(cartToken);
//         if (invoiceDTOs.isEmpty()) {
//             return ResponseEntity.notFound().build();
//         }
//         return ResponseEntity.ok(invoiceDTOs);
//     }

//     @PutMapping("/{orderId}/status")
//     public ResponseEntity<InvoiceDTO> updateOrderStatus(@PathVariable String orderId, @RequestBody UpdateStatusRequestDTO request) {
//         if (request.getStatus() == OrderStatus.CANCELLED) {
//             // Nếu trạng thái là CANCELLED, gọi PaymentService.cancelOrder để xử lý logic hủy đơn hàng
//             paymentService.cancelOrder(orderId);
//             // Sau khi hủy, lấy lại thông tin đơn hàng đã cập nhật để trả về
//             return orderService.getOrderByOrderId(orderId)
//                     .map(order -> ResponseEntity.ok(orderService.mapToInvoiceDTO(order)))
//                     .orElseGet(() -> ResponseEntity.notFound().build());
//         } else {
//             // Nếu không phải CANCELLED, chỉ cập nhật trạng thái đơn hàng
//             InvoiceDTO updatedOrder = orderService.updateOrderStatus(orderId, request.getStatus());
//             return ResponseEntity.ok(updatedOrder);
//         }
//     }
// }
// src/main/java/com/ecommerce/Ecommerce/controller/OrderController.java
package com.ecommerce.Ecommerce.controller;

import com.ecommerce.Ecommerce.annotation.RequireAdminRole;
import com.ecommerce.Ecommerce.annotation.RequireUserRole;
import com.ecommerce.Ecommerce.model.Address;
import com.ecommerce.Ecommerce.model.Order;
import com.ecommerce.Ecommerce.model.OrderStatus;
import com.ecommerce.Ecommerce.model.dto.CartDTO;
import com.ecommerce.Ecommerce.model.dto.InvoiceDTO;
import com.ecommerce.Ecommerce.service.NotificationService;
import com.ecommerce.Ecommerce.service.OrderService;
import com.ecommerce.Ecommerce.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private NotificationService notificationService; // Thêm NotificationService

    public static class OrderRequestDTO {
        private String orderId;
        private Long userId;
        private CartDTO cartDTO;
        private Address shippingAddress;
        private String shippingMethodCode;
        private String couponCode;
        private String customerName;
        private String phoneNumber;
        private String email;

        // Getters and Setters
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public CartDTO getCartDTO() { return cartDTO; }
        public void setCartDTO(CartDTO cartDTO) { this.cartDTO = cartDTO; }
        public Address getShippingAddress() { return shippingAddress; }
        public void setShippingAddress(Address shippingAddress) { this.shippingAddress = shippingAddress; }
        public String getShippingMethodCode() { return shippingMethodCode; }
        public void setShippingMethodCode(String shippingMethodCode) { this.shippingMethodCode = shippingMethodCode; }
        public String getCouponCode() { return couponCode; }
        public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    }

    public static class UpdateStatusRequestDTO {
        private OrderStatus status;

        // Getters and Setters
        public OrderStatus getStatus() { return status; }
        public void setStatus(OrderStatus status) { this.status = status; }
    }

    @GetMapping
    public ResponseEntity<List<InvoiceDTO>> getAllOrders() {
        List<InvoiceDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createOrder(@RequestBody OrderRequestDTO request) {
        Order order = orderService.createOrder(
            request.getOrderId(),
            request.getUserId(),
            request.getCartDTO(),
            request.getShippingAddress(),
            request.getShippingMethodCode(),
            request.getCouponCode(),
            request.getEmail(),
            request.getCustomerName(),
            request.getPhoneNumber()
        );
        // Gửi thông báo với mã đơn hàng
        notificationService.createNotification("Bạn có đơn hàng mới với mã " + order.getOrderId());
        Map<String, String> response = Map.of("orderId", order.getOrderId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<InvoiceDTO> getOrderByOrderId(@PathVariable String orderId) {
        return orderService.getOrderByOrderId(orderId)
                .map(order -> ResponseEntity.ok(orderService.mapToInvoiceDTO(order)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<InvoiceDTO>> getOrdersByUserId(@PathVariable Long userId) {
        List<InvoiceDTO> invoiceDTOs = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(invoiceDTOs);
    }

    @GetMapping("/cart-token/{cartToken}")
    public ResponseEntity<List<InvoiceDTO>> getOrdersByCartToken(@PathVariable String cartToken) {
        List<InvoiceDTO> invoiceDTOs = orderService.getOrdersByCartToken(cartToken);
        if (invoiceDTOs.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(invoiceDTOs);
    }

    @PutMapping("/{orderId}/status")
        @RequireUserRole 
        @RequireAdminRole(roles = {"super_admin", "order_manager"})
    public ResponseEntity<InvoiceDTO> updateOrderStatus(
            @PathVariable String orderId,
            @RequestBody UpdateStatusRequestDTO request) {
        if (request.getStatus() == OrderStatus.CANCELLED) {
            // Nếu trạng thái là CANCELLED, gọi PaymentService.cancelOrder để xử lý logic hủy đơn hàng
            paymentService.cancelOrder(orderId);
            // Gửi thông báo với mã đơn hàng
            notificationService.createNotification("Đơn hàng " + orderId + " đã bị hủy");
            // Sau khi hủy, lấy lại thông tin đơn hàng đã cập nhật để trả về
            return orderService.getOrderByOrderId(orderId)
                    .map(order -> ResponseEntity.ok(orderService.mapToInvoiceDTO(order)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } else {
            // Nếu không phải CANCELLED, chỉ cập nhật trạng thái đơn hàng
            InvoiceDTO updatedOrder = orderService.updateOrderStatus(orderId, request.getStatus());
            // Gửi thông báo với mã đơn hàng và trạng thái mới
            notificationService.createNotification(
                    "Đơn hàng " + orderId + " đã được cập nhật" 
            );
            return ResponseEntity.ok(updatedOrder);
        }
    }
}