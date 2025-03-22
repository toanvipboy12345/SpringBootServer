
// package com.ecommerce.Ecommerce.service;

// import com.ecommerce.Ecommerce.exception.InvalidInputException;
// import com.ecommerce.Ecommerce.model.*;
// import com.ecommerce.Ecommerce.model.dto.CartDTO;
// import com.ecommerce.Ecommerce.model.dto.InvoiceDTO;
// import com.ecommerce.Ecommerce.model.dto.InvoiceDTO.AddressDTO;
// import com.ecommerce.Ecommerce.model.dto.InvoiceDTO.InvoiceItemDTO;
// import com.ecommerce.Ecommerce.repository.*;
// import com.ecommerce.Ecommerce.utils.VNPayUtil;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.util.List;
// import java.util.Optional;
// import java.util.stream.Collectors;

// @Service
// public class PaymentService {

//     @Autowired
//     private PaymentRepository paymentRepository;

//     @Autowired
//     private UserRepository userRepository;

//     @Autowired
//     private TransactionService transactionService;

//     @Autowired
//     private CartRepository cartRepository;

//     @Autowired
//     private CartItemRepository cartItemRepository;

//     @Autowired
//     private VariantSizeRepository variantSizeRepository;

//     @Autowired
//     private VNPayUtil vnpayUtil;

//     @Autowired
//     private OrderService orderService;

//     @Autowired
//     private EmailService emailService;

//     // DTO để trả về Payment và paymentUrl
//     public static class PaymentResponseDTO {
//         private Payment payment;
//         private String paymentUrl;

//         public PaymentResponseDTO(Payment payment, String paymentUrl) {
//             this.payment = payment;
//             this.paymentUrl = paymentUrl;
//         }

//         public Payment getPayment() {
//             return payment;
//         }

//         public String getPaymentUrl() {
//             return paymentUrl;
//         }
//     }

//     // Phương thức xây dựng nội dung email
//     private String buildEmailBody(InvoiceDTO invoiceDTO) {
//         StringBuilder html = new StringBuilder();
//         html.append("<!DOCTYPE html>\n<html lang=\"vi\">\n<head>\n");
//         html.append("<meta charset=\"UTF-8\">\n");
//         html.append("<title>Đơn hàng</title>\n");
//         html.append("<style>\n");
//         html.append("    body { font-family: Arial, sans-serif; color: #333; margin: 0; padding: 20px; background-color: #f4f4f4; }\n");
//         html.append("    .container { width: 100%; max-width: 700px; margin: 0 auto; padding: 15px; background-color: #fff; box-shadow: 0 0 10px rgba(0,0,0,0.1); }\n");
//         html.append("    .table { width: 100%; margin-bottom: 1rem; color: #212529; border-collapse: collapse; }\n");
//         html.append("    .table th, .table td { padding: 0.75rem; vertical-align: top; border: 1px solid #dee2e6; }\n");
//         html.append("    .table thead th { vertical-align: bottom; background-color: #f8f9fa; border-bottom: 2px solid #dee2e6; }\n");
//         html.append("    .table-bordered { border: 1px solid #dee2e6; }\n");
//         html.append("    .text-center { text-align: center; }\n");
//         html.append("    h2 { color: #000; }\n");
//         html.append("    p { margin: 5px 0; }\n");
//         html.append("</style>\n");
//         html.append("</head>\n<body>\n");

//         html.append("<div class=\"container-fluid\">\n");

//         html.append("<h2 class=\"text-center\">Cảm ơn bạn đã đặt hàng!</h2>\n");
//         html.append("<p><strong>Thông tin đơn hàng:</strong></p>\n");
//         html.append("<p>Mã đơn hàng: ").append(invoiceDTO.getOrderId()).append("</p>\n");
//         html.append("<p>Tên khách hàng: ").append(invoiceDTO.getCustomerName()).append("</p>\n");
//         html.append("<p>Số điện thoại: ").append(invoiceDTO.getPhoneNumber()).append("</p>\n");
//         html.append("<p>Phương thức thanh toán: ").append(invoiceDTO.getPaymentMethod()).append("</p>\n");
//         html.append("<p>Tổng tiền: ").append(String.format("%,.0f VND", invoiceDTO.getTotalAmount())).append("</p>\n");

//         html.append("<h3>Chi tiết sản phẩm:</h3>\n");
//         html.append("<table class=\"table table-bordered\">\n");
//         html.append("    <thead>\n");
//         html.append("        <tr>\n");
//         html.append("            <th>Sản phẩm</th>\n");
//         html.append("            <th>Màu sắc</th>\n");
//         html.append("            <th>Size</th>\n");
//         html.append("            <th>Số lượng</th>\n");
//         html.append("            <th>Đơn giá</th>\n");
//         html.append("        </tr>\n");
//         html.append("    </thead>\n");
//         html.append("    <tbody>\n");

//         for (InvoiceItemDTO item : invoiceDTO.getItems()) {
//             html.append("        <tr>\n");
//             html.append("            <td>").append(item.getProductName()).append("</td>\n");
//             html.append("            <td>").append(item.getVariantColor() != null ? item.getVariantColor() : "N/A").append("</td>\n");
//             html.append("            <td>").append(item.getSize()).append("</td>\n");
//             html.append("            <td>").append(item.getQuantity()).append("</td>\n");
//             html.append("            <td>").append(String.format("%,.0f VND", item.getPrice())).append("</td>\n");
//             html.append("        </tr>\n");
//         }

//         html.append("    </tbody>\n");
//         html.append("</table>\n");
//         html.append("<p>Vui lòng kiểm tra thông tin và liên hệ với chúng tôi nếu có thắc mắc.</p>\n");
//         html.append("</div>\n");
//         html.append("</body>\n</html>");

//         return html.toString();
//     }

//     // Phương thức xây dựng email thông báo giao hàng thành công
//     private String buildDeliveryEmailBody(InvoiceDTO invoiceDTO) {
//         StringBuilder html = new StringBuilder();
//         html.append("<!DOCTYPE html>\n<html lang=\"vi\">\n<head>\n");
//         html.append("<meta charset=\"UTF-8\">\n");
//         html.append("<title>Đơn hàng đã được giao</title>\n");
//         html.append("<style>\n");
//         html.append("    body { font-family: Arial, sans-serif; color: #333; margin: 0; padding: 20px; background-color: #f4f4f4; }\n");
//         html.append("    .container { width: 100%; max-width: 700px; margin: 0 auto; padding: 15px; background-color: #fff; box-shadow: 0 0 10px rgba(0,0,0,0.1); }\n");
//         html.append("    .table { width: 100%; margin-bottom: 1rem; color: #212529; border-collapse: collapse; }\n");
//         html.append("    .table th, .table td { padding: 0.75rem; vertical-align: top; border: 1px solid #dee2e6; }\n");
//         html.append("    .table thead th { vertical-align: bottom; background-color: #f8f9fa; border-bottom: 2px solid #dee2e6; }\n");
//         html.append("    .table-bordered { border: 1px solid #dee2e6; }\n");
//         html.append("    .text-center { text-align: center; }\n");
//         html.append("    h2 { color: #000; }\n");
//         html.append("    p { margin: 5px 0; }\n");
//         html.append("</style>\n");
//         html.append("</head>\n<body>\n");

//         html.append("<div class=\"container-fluid\">\n");

//         html.append("<h2 class=\"text-center\">Đơn hàng đã được giao thành công!</h2>\n");
//         html.append("<p><strong>Thông tin đơn hàng:</strong></p>\n");
//         html.append("<p>Mã đơn hàng: ").append(invoiceDTO.getOrderId()).append("</p>\n");
//         html.append("<p>Tên khách hàng: ").append(invoiceDTO.getCustomerName()).append("</p>\n");
//         html.append("<p>Số điện thoại: ").append(invoiceDTO.getPhoneNumber()).append("</p>\n");
//         html.append("<p>Phương thức thanh toán: ").append(invoiceDTO.getPaymentMethod()).append("</p>\n");
//         html.append("<p>Tổng tiền: ").append(String.format("%,.0f VND", invoiceDTO.getTotalAmount())).append("</p>\n");

//         html.append("<h3>Chi tiết sản phẩm:</h3>\n");
//         html.append("<table class=\"table table-bordered\">\n");
//         html.append("    <thead>\n");
//         html.append("        <tr>\n");
//         html.append("            <th>Sản phẩm</th>\n");
//         html.append("            <th>Màu sắc</th>\n");
//         html.append("            <th>Size</th>\n");
//         html.append("            <th>Số lượng</th>\n");
//         html.append("            <th>Đơn giá</th>\n");
//         html.append("        </tr>\n");
//         html.append("    </thead>\n");
//         html.append("    <tbody>\n");

//         for (InvoiceItemDTO item : invoiceDTO.getItems()) {
//             html.append("        <tr>\n");
//             html.append("            <td>").append(item.getProductName()).append("</td>\n");
//             html.append("            <td>").append(item.getVariantColor() != null ? item.getVariantColor() : "N/A").append("</td>\n");
//             html.append("            <td>").append(item.getSize()).append("</td>\n");
//             html.append("            <td>").append(item.getQuantity()).append("</td>\n");
//             html.append("            <td>").append(String.format("%,.0f VND", item.getPrice())).append("</td>\n");
//             html.append("        </tr>\n");
//         }

//         html.append("    </tbody>\n");
//         html.append("</table>\n");
//         html.append("<p>Cảm ơn bạn đã mua sắm tại cửa hàng của chúng tôi!</p>\n");
//         html.append("</div>\n");
//         html.append("</body>\n</html>");

//         return html.toString();
//     }

//     // Phương thức ánh xạ từ Order và Payment sang InvoiceDTO
//     private InvoiceDTO mapToInvoiceDTO(String orderId) {
//         Optional<Order> orderOpt = orderService.getOrderByOrderId(orderId);
//         if (!orderOpt.isPresent()) {
//             throw new InvalidInputException("Order not found for orderId: " + orderId);
//         }
//         Order order = orderOpt.get();
//         Optional<Payment> paymentOpt = paymentRepository.findByOrderId(orderId);
//         if (!paymentOpt.isPresent()) {
//             throw new InvalidInputException("Payment not found for orderId: " + orderId);
//         }
//         Payment payment = paymentOpt.get();

//         String customerIdentifier = order.getCartToken() != null ? order.getCartToken()
//                 : (payment.getUser() != null ? payment.getUser().getId().toString() : null);
//         AddressDTO shippingAddressDTO = new AddressDTO(
//             order.getShippingAddress().getStreet(),
//             order.getShippingAddress().getWard(),
//             order.getShippingAddress().getDistrict(),
//             order.getShippingAddress().getCity(),
//             order.getShippingAddress().getCountry()
//         );

//         List<InvoiceItemDTO> items = order.getItems().stream().map(item -> {
//             return new InvoiceItemDTO(
//                 item.getProduct().getId(),
//                 item.getProduct().getName(),
//                 item.getVariant().getColor(),
//                 item.getSize().getSize(),
//                 item.getQuantity(),
//                 item.getPrice(),
//                 item.getVariant().getMainImage()
//             );
//         }).collect(Collectors.toList());

//         InvoiceDTO invoiceDTO = new InvoiceDTO(
//             order.getOrderId(),
//             customerIdentifier,
//             order.getCustomerName(),
//             order.getEmail(),
//             order.getPhoneNumber(),
//             shippingAddressDTO,
//             order.getShippingMethod().getName(),
//             order.getTotalAmount(),
//             order.getShippingMethod().getShippingFee(),
//             payment.getPaymentMethod(),
//             payment.getStatus(),
//             order.getStatus(),
//             items
//         );

//         invoiceDTO.setCreatedAt(order.getCreatedAt());
//         invoiceDTO.setUpdatedAt(order.getUpdatedAt());

//         return invoiceDTO;
//     }

//     @Transactional(rollbackFor = Exception.class)
//     public PaymentResponseDTO createPayment(String orderId, Long userId, CartDTO cartDTO, PaymentMethod paymentMethod,
//             Address shippingAddress) {
//         System.out.println("Starting createPayment - orderId: " + orderId + ", userId: " + userId + ", paymentMethod: "
//                 + paymentMethod);

//         // Kiểm tra xem Payment đã tồn tại chưa
//         if (paymentRepository.findByOrderId(orderId).isPresent()) {
//             System.out.println("Payment already exists for orderId: " + orderId);
//             throw new InvalidInputException("Payment already exists for orderId: " + orderId);
//         }
//         System.out.println("No existing payment found for orderId: " + orderId);

//         // Kiểm tra CartDTO
//         if (cartDTO == null || cartDTO.getItems() == null || cartDTO.getItems().isEmpty()) {
//             throw new InvalidInputException("Cart is empty or invalid for orderId: " + orderId);
//         }

//         // Lấy Order đã được tạo trước đó
//         Optional<Order> orderOpt = orderService.getOrderByOrderId(orderId);
//         if (!orderOpt.isPresent()) {
//             throw new InvalidInputException(
//                     "Order not found for orderId: " + orderId + ". Please create the order first.");
//         }
//         Order order = orderOpt.get();
//         double amount = order.getTotalAmount();

//         // Tạo Payment
//         Payment payment = new Payment();
//         payment.setOrderId(orderId);
//         payment.setCartToken(cartDTO.getCartToken());
//         System.out.println("Payment initialized - orderId: " + orderId + ", cartToken: " + cartDTO.getCartToken());

//         if (userId != null) {
//             System.out.println("Fetching user with id: " + userId);
//             User user = userRepository.findById(userId)
//                     .orElseThrow(() -> {
//                         System.out.println("User not found with id: " + userId);
//                         return new InvalidInputException("User not found with id: " + userId);
//                     });
//             payment.setUser(user);
//             System.out.println("User found and set for payment - userId: " + userId);
//         } else {
//             payment.setUser(null);
//             System.out.println("No userId provided, setting user to null");
//         }

//         payment.setAmount(amount);
//         payment.setPaymentMethod(paymentMethod);
//         payment.setStatus(PaymentStatus.PENDING);
//         System.out.println("Payment set - amount: " + amount + ", paymentMethod: " + paymentMethod + ", status: "
//                 + PaymentStatus.PENDING);

//         // Kiểm tra kho
//         if (paymentMethod == PaymentMethod.VNPAY || paymentMethod == PaymentMethod.COD) {
//             System.out.println("Checking stock for paymentMethod: " + paymentMethod);
//             for (CartDTO.CartItemDTO cartItemDTO : cartDTO.getItems()) {
//                 VariantSize size = variantSizeRepository.findById(cartItemDTO.getSizeId())
//                         .orElseThrow(
//                                 () -> new InvalidInputException("Size not found with id: " + cartItemDTO.getSizeId()));
//                 if (size.getQuantity() < cartItemDTO.getQuantity()) {
//                     System.out.println("Insufficient stock for variant: " + cartItemDTO.getVariant().getId()
//                             + ", size: " + cartItemDTO.getSize());
//                     throw new InvalidInputException("Insufficient stock for variant: "
//                             + cartItemDTO.getVariant().getId() + ", size: " + cartItemDTO.getSize());
//                 }
//                 System.out.println("Stock sufficient for variant: " + cartItemDTO.getVariant().getId());
//             }
//         } else {
//             System.out.println("No stock check required for paymentMethod: " + paymentMethod);
//         }

//         // Tạo URL thanh toán nếu là VNPAY
//         String paymentUrl = null;
//         if (paymentMethod == PaymentMethod.VNPAY) {
//             System.out.println("Creating VNPAY payment URL for orderId: " + orderId);
//             try {
//                 paymentUrl = vnpayUtil.createPaymentUrl(orderId, amount, "127.0.0.1", null);
//                 System.out.println("VNPAY payment URL created: " + paymentUrl);
//             } catch (Exception e) {
//                 System.out.println("Failed to create VNPAY payment URL: " + e.getMessage());
//                 throw new InvalidInputException("Failed to create VNPAY payment URL: " + e.getMessage());
//             }
//         }

//         // Lưu Payment
//         Payment savedPayment = paymentRepository.save(payment);
//         System.out.println("Payment saved successfully - paymentId: " + savedPayment.getId() + ", orderId: "
//                 + savedPayment.getOrderId() + ", status: " + savedPayment.getStatus());

//         // Liên kết Payment với Order
//         orderService.linkPaymentToOrder(orderId, savedPayment);

//         // Nếu là COD, cập nhật trạng thái đơn hàng thành CONFIRMED, trừ kho, làm trống giỏ hàng và gửi email
//         if (paymentMethod == PaymentMethod.COD) {
//             orderService.updateOrderStatus(orderId, OrderStatus.CONFIRMED);
//             System.out.println("Order status updated to CONFIRMED for COD orderId: " + orderId);

//             // Trừ kho
//             try {
//                 System.out.println("Attempting to deduct stock for COD orderId: " + orderId);
//                 deductStockAfterPayment(savedPayment);
//             } catch (Exception e) {
//                 System.out.println("Failed to deduct stock for COD orderId: " + orderId + ", error: " + e.getMessage());
//                 throw new InvalidInputException("Failed to deduct stock: " + e.getMessage());
//             }

//             // Làm trống giỏ hàng
//             System.out.println("Clearing cart for COD orderId: " + orderId);
//             clearCartAfterPayment(savedPayment);

//             // Gửi email thông báo đặt hàng thành công
//             System.out.println("Preparing to send email for COD orderId: " + orderId);
//             InvoiceDTO invoiceDTO = mapToInvoiceDTO(orderId);
//             String emailBody = buildEmailBody(invoiceDTO);
//             String recipientEmail = invoiceDTO.getEmail();
//             String subject = String.format("Xác nhận đơn hàng #%s - %s (%s)",
//                 orderId, invoiceDTO.getCustomerName(), java.time.LocalDate.now());
//             emailService.sendEmail(recipientEmail, subject, emailBody);
//             System.out.println("Email sent to: " + recipientEmail + " for COD orderId: " + orderId);
//         }

//         // Trả về Payment và paymentUrl (nếu có)
//         return new PaymentResponseDTO(savedPayment, paymentUrl);
//     }

//     @Transactional(rollbackFor = Exception.class)
//     public Payment updatePaymentStatus(String orderId, String transactionId, PaymentStatus status, String responseCode,
//             String signature) {
//         Optional<Payment> paymentOpt = paymentRepository.findByOrderId(orderId);
//         if (!paymentOpt.isPresent()) {
//             throw new InvalidInputException("Payment not found for orderId: " + orderId);
//         }

//         Payment payment = paymentOpt.get();
//         payment.setTransactionId(transactionId);
//         payment.setStatus(status);

//         System.out.println("Updating payment status to: " + status + " for orderId: " + orderId);

//         Payment updatedPayment = paymentRepository.save(payment);

//         // Cập nhật trạng thái Order
//         if (status == PaymentStatus.SUCCESS) {
//             orderService.updateOrderStatus(orderId, OrderStatus.CONFIRMED);
//             System.out.println("Order status updated to CONFIRMED for orderId: " + orderId);
//             System.out.println("Creating transaction for orderId: " + orderId);
//             transactionService.createTransaction(payment, transactionId, responseCode, signature);
//             try {
//                 System.out.println("Attempting to deduct stock for orderId: " + orderId);
//                 deductStockAfterPayment(payment);
//             } catch (Exception e) {
//                 System.out.println("Failed to deduct stock for orderId: " + orderId + ", error: " + e.getMessage()
//                         + ". Continuing with payment success.");
//             }
//             // Làm trống giỏ hàng sau khi thanh toán thành công
//             System.out.println("Clearing cart after successful payment for orderId: " + orderId);
//             clearCartAfterPayment(payment);

//             // Gửi email thông báo
//             System.out.println("Preparing to send email for orderId: " + orderId);
//             InvoiceDTO invoiceDTO = mapToInvoiceDTO(orderId);
//             String emailBody = buildEmailBody(invoiceDTO);
//             String recipientEmail = invoiceDTO.getEmail();
//             String subject = String.format("Xác nhận đơn hàng #%s - %s (%s)",
//                 orderId, invoiceDTO.getCustomerName(), java.time.LocalDate.now());
//             emailService.sendEmail(recipientEmail, subject, emailBody);
//             System.out.println("Email sent to: " + recipientEmail + " for orderId: " + orderId);
//         } else {
//             orderService.updateOrderStatus(orderId, OrderStatus.PENDING);
//             System.out.println("Order status updated to PENDING for orderId: " + orderId);
//         }

//         return updatedPayment;
//     }

//     @Transactional(rollbackFor = Exception.class)
//     public Payment confirmCodPayment(String orderId) {
//         Optional<Payment> paymentOpt = paymentRepository.findByOrderId(orderId);
//         if (!paymentOpt.isPresent()) {
//             throw new InvalidInputException("Payment not found for orderId: " + orderId);
//         }

//         Payment payment = paymentOpt.get();
//         if (payment.getPaymentMethod() != PaymentMethod.COD) {
//             throw new InvalidInputException("This payment is not COD for orderId: " + orderId);
//         }
//         if (payment.getStatus() != PaymentStatus.PENDING) {
//             throw new InvalidInputException("Payment is not in PENDING status for orderId: " + orderId);
//         }

//         // Cập nhật trạng thái thanh toán thành SUCCESS khi xác nhận COD (giao hàng thành công)
//         payment.setStatus(PaymentStatus.SUCCESS);
//         Payment updatedPayment = paymentRepository.save(payment);
//         System.out.println("Payment status updated to SUCCESS for COD orderId: " + orderId);

//         // Cập nhật trạng thái đơn hàng thành DELIVERED
//         orderService.updateOrderStatus(orderId, OrderStatus.DELIVERED);
//         System.out.println("Order status updated to DELIVERED for COD orderId: " + orderId);

//         // Tạo giao dịch (nếu cần)
//         transactionService.createTransaction(payment, null, "00", "");

//         // Gửi email thông báo giao hàng thành công
//         System.out.println("Preparing to send delivery email for COD orderId: " + orderId);
//         InvoiceDTO invoiceDTO = mapToInvoiceDTO(orderId);
//         String emailBody = buildDeliveryEmailBody(invoiceDTO);
//         String recipientEmail = invoiceDTO.getEmail();
//         String subject = String.format("Đơn hàng #%s đã được giao thành công - %s (%s)",
//             orderId, invoiceDTO.getCustomerName(), java.time.LocalDate.now());
//         emailService.sendEmail(recipientEmail, subject, emailBody);
//         System.out.println("Delivery email sent to: " + recipientEmail + " for COD orderId: " + orderId);

//         return updatedPayment;
//     }

//     @Transactional(rollbackFor = Exception.class)
//     private void deductStockAfterPayment(Payment payment) {
//         Optional<Cart> cartOptional;
//         if (payment.getUser() != null) {
//             Long userId = payment.getUser().getId();
//             cartOptional = cartRepository.findByUserId(userId);
//             System.out.println(
//                     "Searching cart by userId: " + userId + ", result: " + cartOptional.map(Cart::getId).orElse(null));
//         } else if (payment.getCartToken() != null && !payment.getCartToken().isEmpty()) {
//             cartOptional = cartRepository.findByCartToken(payment.getCartToken());
//             System.out.println("Searching cart by cartToken: " + payment.getCartToken() + ", result: "
//                     + cartOptional.map(Cart::getId).orElse(null));
//         } else {
//             System.out.println("No cart or user info for payment: " + payment.getOrderId());
//             throw new InvalidInputException("No cart or user information found for payment: " + payment.getOrderId());
//         }

//         Cart cart = cartOptional.orElseThrow(() -> {
//             System.out
//                     .println("Cart not found for payment: " + payment.getOrderId() + ", cartOptional: " + cartOptional);
//             return new InvalidInputException("Cart not found for payment: " + payment.getOrderId());
//         });

//         List<CartItem> cartItems = cartItemRepository.findByCart(cart);
//         if (cartItems.isEmpty()) {
//             System.out.println("Cart is empty for payment: " + payment.getOrderId());
//             return;
//         }

//         for (CartItem cartItem : cartItems) {
//             ProductVariant variant = cartItem.getVariant();
//             VariantSize variantSize = cartItem.getSize();
//             Integer quantityToDeduct = cartItem.getQuantity();

//             System.out.println("Deducting stock for variant: " + variant.getId() + ", size: " + variantSize.getSize()
//                     + ", quantity: " + quantityToDeduct);
//             if (variantSize.getQuantity() < quantityToDeduct) {
//                 throw new InvalidInputException(
//                         "Insufficient stock for variant: " + variant.getId() + ", size: " + variantSize.getSize());
//             }

//             variantSize.setQuantity(variantSize.getQuantity() - quantityToDeduct);
//             variantSizeRepository.save(variantSize);
//         }

//         System.out.println("Stock deduction completed for payment: " + payment.getOrderId());
//     }

//     @Transactional(rollbackFor = Exception.class)
//     private void clearCartAfterPayment(Payment payment) {
//         Optional<Cart> cartOptional;
//         if (payment.getUser() != null) {
//             Long userId = payment.getUser().getId();
//             cartOptional = cartRepository.findByUserId(userId);
//             System.out.println(
//                     "Searching cart by userId: " + userId + ", result: " + cartOptional.map(Cart::getId).orElse(null));
//         } else if (payment.getCartToken() != null && !payment.getCartToken().isEmpty()) {
//             cartOptional = cartRepository.findByCartToken(payment.getCartToken());
//             System.out.println("Searching cart by cartToken: " + payment.getCartToken() + ", result: "
//                     + cartOptional.map(Cart::getId).orElse(null));
//         } else {
//             System.out.println("No cart or user info for payment: " + payment.getOrderId());
//             return;
//         }

//         if (cartOptional.isPresent()) {
//             Cart cart = cartOptional.get();
//             List<CartItem> cartItems = cartItemRepository.findByCart(cart);
//             System.out.println("Clearing cart for payment: " + payment.getOrderId() + ", cartId: " + cart.getId());
//             cartItemRepository.deleteAll(cartItems);
//             cartRepository.delete(cart);
//         } else {
//             System.out.println("No cart found to clear for payment: " + payment.getOrderId());
//         }
//     }

//     public Optional<Payment> getPaymentByOrderId(String orderId) {
//         return paymentRepository.findByOrderId(orderId);
//     }
// }
package com.ecommerce.Ecommerce.service;

import com.ecommerce.Ecommerce.exception.InvalidInputException;
import com.ecommerce.Ecommerce.model.*;
import com.ecommerce.Ecommerce.model.dto.CartDTO;
import com.ecommerce.Ecommerce.model.dto.InvoiceDTO;
import com.ecommerce.Ecommerce.model.dto.InvoiceDTO.AddressDTO;
import com.ecommerce.Ecommerce.model.dto.InvoiceDTO.InvoiceItemDTO;
import com.ecommerce.Ecommerce.repository.*;
import com.ecommerce.Ecommerce.utils.VNPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private VariantSizeRepository variantSizeRepository;

    @Autowired
    private VNPayUtil vnpayUtil;

    @Autowired
    private OrderService orderService;

    @Autowired
    private EmailService emailService;

    // DTO để trả về Payment và paymentUrl
    public static class PaymentResponseDTO {
        private Payment payment;
        private String paymentUrl;

        public PaymentResponseDTO(Payment payment, String paymentUrl) {
            this.payment = payment;
            this.paymentUrl = paymentUrl;
        }

        public Payment getPayment() {
            return payment;
        }

        public String getPaymentUrl() {
            return paymentUrl;
        }
    }

    // Phương thức xây dựng nội dung email xác nhận đặt hàng
    private String buildEmailBody(InvoiceDTO invoiceDTO) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html lang=\"vi\">\n<head>\n");
        html.append("<meta charset=\"UTF-8\">\n");
        html.append("<title>Đơn hàng</title>\n");
        html.append("<style>\n");
        html.append("    body { font-family: Arial, sans-serif; color: #333; margin: 0; padding: 20px; background-color: #f4f4f4; }\n");
        html.append("    .container { width: 100%; max-width: 700px; margin: 0 auto; padding: 15px; background-color: #fff; box-shadow: 0 0 10px rgba(0,0,0,0.1); }\n");
        html.append("    .table { width: 100%; margin-bottom: 1rem; color: #212529; border-collapse: collapse; }\n");
        html.append("    .table th, .table td { padding: 0.75rem; vertical-align: top; border: 1px solid #dee2e6; }\n");
        html.append("    .table thead th { vertical-align: bottom; background-color: #f8f9fa; border-bottom: 2px solid #dee2e6; }\n");
        html.append("    .table-bordered { border: 1px solid #dee2e6; }\n");
        html.append("    .text-center { text-align: center; }\n");
        html.append("    h2 { color: #000; }\n");
        html.append("    p { margin: 5px 0; }\n");
        html.append("</style>\n");
        html.append("</head>\n<body>\n");

        html.append("<div class=\"container-fluid\">\n");

        html.append("<h2 class=\"text-center\">Cảm ơn bạn đã đặt hàng!</h2>\n");
        html.append("<p><strong>Thông tin đơn hàng:</strong></p>\n");
        html.append("<p>Mã đơn hàng: ").append(invoiceDTO.getOrderId()).append("</p>\n");
        html.append("<p>Tên khách hàng: ").append(invoiceDTO.getCustomerName()).append("</p>\n");
        html.append("<p>Số điện thoại: ").append(invoiceDTO.getPhoneNumber()).append("</p>\n");
        html.append("<p>Phương thức thanh toán: ").append(invoiceDTO.getPaymentMethod()).append("</p>\n");
        html.append("<p>Tổng tiền: ").append(String.format("%,.0f VND", invoiceDTO.getTotalAmount())).append("</p>\n");

        html.append("<h3>Chi tiết sản phẩm:</h3>\n");
        html.append("<table class=\"table table-bordered\">\n");
        html.append("    <thead>\n");
        html.append("        <tr>\n");
        html.append("            <th>Sản phẩm</th>\n");
        html.append("            <th>Màu sắc</th>\n");
        html.append("            <th>Size</th>\n");
        html.append("            <th>Số lượng</th>\n");
        html.append("            <th>Đơn giá</th>\n");
        html.append("        </tr>\n");
        html.append("    </thead>\n");
        html.append("    <tbody>\n");

        for (InvoiceItemDTO item : invoiceDTO.getItems()) {
            html.append("        <tr>\n");
            html.append("            <td>").append(item.getProductName()).append("</td>\n");
            html.append("            <td>").append(item.getVariantColor() != null ? item.getVariantColor() : "N/A").append("</td>\n");
            html.append("            <td>").append(item.getSize()).append("</td>\n");
            html.append("            <td>").append(item.getQuantity()).append("</td>\n");
            html.append("            <td>").append(String.format("%,.0f VND", item.getPrice())).append("</td>\n");
            html.append("        </tr>\n");
        }

        html.append("    </tbody>\n");
        html.append("</table>\n");
        html.append("<p>Vui lòng kiểm tra thông tin và liên hệ với chúng tôi nếu có thắc mắc.</p>\n");
        html.append("</div>\n");
        html.append("</body>\n</html>");

        return html.toString();
    }

    // Phương thức xây dựng email thông báo giao hàng thành công
    private String buildDeliveryEmailBody(InvoiceDTO invoiceDTO) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html lang=\"vi\">\n<head>\n");
        html.append("<meta charset=\"UTF-8\">\n");
        html.append("<title>Đơn hàng đã được giao</title>\n");
        html.append("<style>\n");
        html.append("    body { font-family: Arial, sans-serif; color: #333; margin: 0; padding: 20px; background-color: #f4f4f4; }\n");
        html.append("    .container { width: 100%; max-width: 700px; margin: 0 auto; padding: 15px; background-color: #fff; box-shadow: 0 0 10px rgba(0,0,0,0.1); }\n");
        html.append("    .table { width: 100%; margin-bottom: 1rem; color: #212529; border-collapse: collapse; }\n");
        html.append("    .table th, .table td { padding: 0.75rem; vertical-align: top; border: 1px solid #dee2e6; }\n");
        html.append("    .table thead th { vertical-align: bottom; background-color: #f8f9fa; border-bottom: 2px solid #dee2e6; }\n");
        html.append("    .table-bordered { border: 1px solid #dee2e6; }\n");
        html.append("    .text-center { text-align: center; }\n");
        html.append("    h2 { color: #000; }\n");
        html.append("    p { margin: 5px 0; }\n");
        html.append("</style>\n");
        html.append("</head>\n<body>\n");

        html.append("<div class=\"container-fluid\">\n");

        html.append("<h2 class=\"text-center\">Đơn hàng đã được giao thành công!</h2>\n");
        html.append("<p><strong>Thông tin đơn hàng:</strong></p>\n");
        html.append("<p>Mã đơn hàng: ").append(invoiceDTO.getOrderId()).append("</p>\n");
        html.append("<p>Tên khách hàng: ").append(invoiceDTO.getCustomerName()).append("</p>\n");
        html.append("<p>Số điện thoại: ").append(invoiceDTO.getPhoneNumber()).append("</p>\n");
        html.append("<p>Phương thức thanh toán: ").append(invoiceDTO.getPaymentMethod()).append("</p>\n");
        html.append("<p>Tổng tiền: ").append(String.format("%,.0f VND", invoiceDTO.getTotalAmount())).append("</p>\n");

        html.append("<h3>Chi tiết sản phẩm:</h3>\n");
        html.append("<table class=\"table table-bordered\">\n");
        html.append("    <thead>\n");
        html.append("        <tr>\n");
        html.append("            <th>Sản phẩm</th>\n");
        html.append("            <th>Màu sắc</th>\n");
        html.append("            <th>Size</th>\n");
        html.append("            <th>Số lượng</th>\n");
        html.append("            <th>Đơn giá</th>\n");
        html.append("        </tr>\n");
        html.append("    </thead>\n");
        html.append("    <tbody>\n");

        for (InvoiceItemDTO item : invoiceDTO.getItems()) {
            html.append("        <tr>\n");
            html.append("            <td>").append(item.getProductName()).append("</td>\n");
            html.append("            <td>").append(item.getVariantColor() != null ? item.getVariantColor() : "N/A").append("</td>\n");
            html.append("            <td>").append(item.getSize()).append("</td>\n");
            html.append("            <td>").append(item.getQuantity()).append("</td>\n");
            html.append("            <td>").append(String.format("%,.0f VND", item.getPrice())).append("</td>\n");
            html.append("        </tr>\n");
        }

        html.append("    </tbody>\n");
        html.append("</table>\n");
        html.append("<p>Cảm ơn bạn đã mua sắm tại cửa hàng của chúng tôi!</p>\n");
        html.append("</div>\n");
        html.append("</body>\n</html>");

        return html.toString();
    }

    // Phương thức xây dựng email thông báo hủy đơn hàng
    private String buildCancellationEmailBody(InvoiceDTO invoiceDTO) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html lang=\"vi\">\n<head>\n");
        html.append("<meta charset=\"UTF-8\">\n");
        html.append("<title>Đơn hàng đã bị hủy</title>\n");
        html.append("<style>\n");
        html.append("    body { font-family: Arial, sans-serif; color: #333; margin: 0; padding: 20px; background-color: #f4f4f4; }\n");
        html.append("    .container { width: 100%; max-width: 700px; margin: 0 auto; padding: 15px; background-color: #fff; box-shadow: 0 0 10px rgba(0,0,0,0.1); }\n");
        html.append("    .table { width: 100%; margin-bottom: 1rem; color: #212529; border-collapse: collapse; }\n");
        html.append("    .table th, .table td { padding: 0.75rem; vertical-align: top; border: 1px solid #dee2e6; }\n");
        html.append("    .table thead th { vertical-align: bottom; background-color: #f8f9fa; border-bottom: 2px solid #dee2e6; }\n");
        html.append("    .table-bordered { border: 1px solid #dee2e6; }\n");
        html.append("    .text-center { text-align: center; }\n");
        html.append("    h2 { color: #000; }\n");
        html.append("    p { margin: 5px 0; }\n");
        html.append("</style>\n");
        html.append("</head>\n<body>\n");

        html.append("<div class=\"container-fluid\">\n");

        html.append("<h2 class=\"text-center\">Đơn hàng của bạn đã bị hủy</h2>\n");
        html.append("<p><strong>Thông tin đơn hàng:</strong></p>\n");
        html.append("<p>Mã đơn hàng: ").append(invoiceDTO.getOrderId()).append("</p>\n");
        html.append("<p>Tên khách hàng: ").append(invoiceDTO.getCustomerName()).append("</p>\n");
        html.append("<p>Số điện thoại: ").append(invoiceDTO.getPhoneNumber()).append("</p>\n");
        html.append("<p>Phương thức thanh toán: ").append(invoiceDTO.getPaymentMethod()).append("</p>\n");
        html.append("<p>Tổng tiền: ").append(String.format("%,.0f VND", invoiceDTO.getTotalAmount())).append("</p>\n");

        html.append("<h3>Chi tiết sản phẩm:</h3>\n");
        html.append("<table class=\"table table-bordered\">\n");
        html.append("    <thead>\n");
        html.append("        <tr>\n");
        html.append("            <th>Sản phẩm</th>\n");
        html.append("            <th>Màu sắc</th>\n");
        html.append("            <th>Size</th>\n");
        html.append("            <th>Số lượng</th>\n");
        html.append("            <th>Đơn giá</th>\n");
        html.append("        </tr>\n");
        html.append("    </thead>\n");
        html.append("    <tbody>\n");

        for (InvoiceItemDTO item : invoiceDTO.getItems()) {
            html.append("        <tr>\n");
            html.append("            <td>").append(item.getProductName()).append("</td>\n");
            html.append("            <td>").append(item.getVariantColor() != null ? item.getVariantColor() : "N/A").append("</td>\n");
            html.append("            <td>").append(item.getSize()).append("</td>\n");
            html.append("            <td>").append(item.getQuantity()).append("</td>\n");
            html.append("            <td>").append(String.format("%,.0f VND", item.getPrice())).append("</td>\n");
            html.append("        </tr>\n");
        }

        html.append("    </tbody>\n");
        html.append("</table>\n");
        html.append("<p>Đơn hàng của bạn đã bị hủy. Nếu có thắc mắc, vui lòng liên hệ với chúng tôi.</p>\n");
        html.append("</div>\n");
        html.append("</body>\n</html>");

        return html.toString();
    }

    // Phương thức ánh xạ từ Order và Payment sang InvoiceDTO
    private InvoiceDTO mapToInvoiceDTO(String orderId) {
        Optional<Order> orderOpt = orderService.getOrderByOrderId(orderId);
        if (!orderOpt.isPresent()) {
            throw new InvalidInputException("Order not found for orderId: " + orderId);
        }
        Order order = orderOpt.get();
        Optional<Payment> paymentOpt = paymentRepository.findByOrderId(orderId);
        if (!paymentOpt.isPresent()) {
            throw new InvalidInputException("Payment not found for orderId: " + orderId);
        }
        Payment payment = paymentOpt.get();

        String customerIdentifier = order.getCartToken() != null ? order.getCartToken()
                : (payment.getUser() != null ? payment.getUser().getId().toString() : null);
        AddressDTO shippingAddressDTO = new AddressDTO(
            order.getShippingAddress().getStreet(),
            order.getShippingAddress().getWard(),
            order.getShippingAddress().getDistrict(),
            order.getShippingAddress().getCity(),
            order.getShippingAddress().getCountry()
        );

        List<InvoiceItemDTO> items = order.getItems().stream().map(item -> {
            return new InvoiceItemDTO(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getVariant().getColor(),
                item.getSize().getSize(),
                item.getQuantity(),
                item.getPrice(),
                item.getVariant().getMainImage()
            );
        }).collect(Collectors.toList());

        InvoiceDTO invoiceDTO = new InvoiceDTO(
            order.getOrderId(),
            customerIdentifier,
            order.getCustomerName(),
            order.getEmail(),
            order.getPhoneNumber(),
            shippingAddressDTO,
            order.getShippingMethod().getName(),
            order.getTotalAmount(),
            order.getShippingMethod().getShippingFee(),
            payment.getPaymentMethod(),
            payment.getStatus(),
            order.getStatus(),
            items
        );

        invoiceDTO.setCreatedAt(order.getCreatedAt());
        invoiceDTO.setUpdatedAt(order.getUpdatedAt());

        return invoiceDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    public PaymentResponseDTO createPayment(String orderId, Long userId, CartDTO cartDTO, PaymentMethod paymentMethod,
            Address shippingAddress) {
        System.out.println("Starting createPayment - orderId: " + orderId + ", userId: " + userId + ", paymentMethod: "
                + paymentMethod);

        // Kiểm tra xem Payment đã tồn tại chưa
        if (paymentRepository.findByOrderId(orderId).isPresent()) {
            System.out.println("Payment already exists for orderId: " + orderId);
            throw new InvalidInputException("Payment already exists for orderId: " + orderId);
        }
        System.out.println("No existing payment found for orderId: " + orderId);

        // Kiểm tra CartDTO
        if (cartDTO == null || cartDTO.getItems() == null || cartDTO.getItems().isEmpty()) {
            throw new InvalidInputException("Cart is empty or invalid for orderId: " + orderId);
        }

        // Lấy Order đã được tạo trước đó
        Optional<Order> orderOpt = orderService.getOrderByOrderId(orderId);
        if (!orderOpt.isPresent()) {
            throw new InvalidInputException(
                    "Order not found for orderId: " + orderId + ". Please create the order first.");
        }
        Order order = orderOpt.get();
        double amount = order.getTotalAmount();

        // Tạo Payment
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setCartToken(cartDTO.getCartToken());
        System.out.println("Payment initialized - orderId: " + orderId + ", cartToken: " + cartDTO.getCartToken());

        if (userId != null) {
            System.out.println("Fetching user with id: " + userId);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        System.out.println("User not found with id: " + userId);
                        return new InvalidInputException("User not found with id: " + userId);
                    });
            payment.setUser(user);
            System.out.println("User found and set for payment - userId: " + userId);
        } else {
            payment.setUser(null);
            System.out.println("No userId provided, setting user to null");
        }

        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus(PaymentStatus.PENDING);
        System.out.println("Payment set - amount: " + amount + ", paymentMethod: " + paymentMethod + ", status: "
                + PaymentStatus.PENDING);

        // Kiểm tra kho
        if (paymentMethod == PaymentMethod.VNPAY || paymentMethod == PaymentMethod.COD) {
            System.out.println("Checking stock for paymentMethod: " + paymentMethod);
            for (CartDTO.CartItemDTO cartItemDTO : cartDTO.getItems()) {
                VariantSize size = variantSizeRepository.findById(cartItemDTO.getSizeId())
                        .orElseThrow(
                                () -> new InvalidInputException("Size not found with id: " + cartItemDTO.getSizeId()));
                if (size.getQuantity() < cartItemDTO.getQuantity()) {
                    System.out.println("Insufficient stock for variant: " + cartItemDTO.getVariant().getId()
                            + ", size: " + cartItemDTO.getSize());
                    throw new InvalidInputException("Insufficient stock for variant: "
                            + cartItemDTO.getVariant().getId() + ", size: " + cartItemDTO.getSize());
                }
                System.out.println("Stock sufficient for variant: " + cartItemDTO.getVariant().getId());
            }
        } else {
            System.out.println("No stock check required for paymentMethod: " + paymentMethod);
        }

        // Tạo URL thanh toán nếu là VNPAY
        String paymentUrl = null;
        if (paymentMethod == PaymentMethod.VNPAY) {
            System.out.println("Creating VNPAY payment URL for orderId: " + orderId);
            try {
                paymentUrl = vnpayUtil.createPaymentUrl(orderId, amount, "127.0.0.1", null);
                System.out.println("VNPAY payment URL created: " + paymentUrl);
            } catch (Exception e) {
                System.out.println("Failed to create VNPAY payment URL: " + e.getMessage());
                throw new InvalidInputException("Failed to create VNPAY payment URL: " + e.getMessage());
            }
        }

        // Lưu Payment
        Payment savedPayment = paymentRepository.save(payment);
        System.out.println("Payment saved successfully - paymentId: " + savedPayment.getId() + ", orderId: "
                + savedPayment.getOrderId() + ", status: " + savedPayment.getStatus());

        // Liên kết Payment với Order
        orderService.linkPaymentToOrder(orderId, savedPayment);

        // Nếu là COD, cập nhật trạng thái đơn hàng thành CONFIRMED, trừ kho, làm trống giỏ hàng và gửi email
        if (paymentMethod == PaymentMethod.COD) {
            orderService.updateOrderStatus(orderId, OrderStatus.CONFIRMED);
            System.out.println("Order status updated to CONFIRMED for COD orderId: " + orderId);

            // Trừ kho
            try {
                System.out.println("Attempting to deduct stock for COD orderId: " + orderId);
                deductStockAfterPayment(savedPayment);
            } catch (Exception e) {
                System.out.println("Failed to deduct stock for COD orderId: " + orderId + ", error: " + e.getMessage());
                throw new InvalidInputException("Failed to deduct stock: " + e.getMessage());
            }

            // Làm trống giỏ hàng
            System.out.println("Clearing cart for COD orderId: " + orderId);
            clearCartAfterPayment(savedPayment);

            // Gửi email thông báo đặt hàng thành công
            System.out.println("Preparing to send email for COD orderId: " + orderId);
            InvoiceDTO invoiceDTO = mapToInvoiceDTO(orderId);
            String emailBody = buildEmailBody(invoiceDTO);
            String recipientEmail = invoiceDTO.getEmail();
            String subject = String.format("Xác nhận đơn hàng #%s - %s (%s)",
                orderId, invoiceDTO.getCustomerName(), java.time.LocalDate.now());
            emailService.sendEmail(recipientEmail, subject, emailBody);
            System.out.println("Email sent to: " + recipientEmail + " for COD orderId: " + orderId);
        }

        // Trả về Payment và paymentUrl (nếu có)
        return new PaymentResponseDTO(savedPayment, paymentUrl);
    }

    @Transactional(rollbackFor = Exception.class)
    public Payment updatePaymentStatus(String orderId, String transactionId, PaymentStatus status, String responseCode,
            String signature) {
        Optional<Payment> paymentOpt = paymentRepository.findByOrderId(orderId);
        if (!paymentOpt.isPresent()) {
            throw new InvalidInputException("Payment not found for orderId: " + orderId);
        }

        Payment payment = paymentOpt.get();
        payment.setTransactionId(transactionId);
        payment.setStatus(status);

        System.out.println("Updating payment status to: " + status + " for orderId: " + orderId);

        Payment updatedPayment = paymentRepository.save(payment);

        // Cập nhật trạng thái Order
        if (status == PaymentStatus.SUCCESS) {
            orderService.updateOrderStatus(orderId, OrderStatus.CONFIRMED);
            System.out.println("Order status updated to CONFIRMED for orderId: " + orderId);
            System.out.println("Creating transaction for orderId: " + orderId);
            transactionService.createTransaction(payment, transactionId, responseCode, signature);
            try {
                System.out.println("Attempting to deduct stock for orderId: " + orderId);
                deductStockAfterPayment(payment);
            } catch (Exception e) {
                System.out.println("Failed to deduct stock for orderId: " + orderId + ", error: " + e.getMessage()
                        + ". Continuing with payment success.");
            }
            // Làm trống giỏ hàng sau khi thanh toán thành công
            System.out.println("Clearing cart after successful payment for orderId: " + orderId);
            clearCartAfterPayment(payment);

            // Gửi email thông báo
            System.out.println("Preparing to send email for orderId: " + orderId);
            InvoiceDTO invoiceDTO = mapToInvoiceDTO(orderId);
            String emailBody = buildEmailBody(invoiceDTO);
            String recipientEmail = invoiceDTO.getEmail();
            String subject = String.format("Xác nhận đơn hàng #%s - %s (%s)",
                orderId, invoiceDTO.getCustomerName(), java.time.LocalDate.now());
            emailService.sendEmail(recipientEmail, subject, emailBody);
            System.out.println("Email sent to: " + recipientEmail + " for orderId: " + orderId);
        } else {
            orderService.updateOrderStatus(orderId, OrderStatus.PENDING);
            System.out.println("Order status updated to PENDING for orderId: " + orderId);
        }

        return updatedPayment;
    }

    @Transactional(rollbackFor = Exception.class)
    public Payment confirmCodPayment(String orderId) {
        Optional<Payment> paymentOpt = paymentRepository.findByOrderId(orderId);
        if (!paymentOpt.isPresent()) {
            throw new InvalidInputException("Payment not found for orderId: " + orderId);
        }

        Payment payment = paymentOpt.get();
        if (payment.getPaymentMethod() != PaymentMethod.COD) {
            throw new InvalidInputException("This payment is not COD for orderId: " + orderId);
        }
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new InvalidInputException("Payment is not in PENDING status for orderId: " + orderId);
        }

        // Cập nhật trạng thái thanh toán thành SUCCESS khi xác nhận COD (giao hàng thành công)
        payment.setStatus(PaymentStatus.SUCCESS);
        Payment updatedPayment = paymentRepository.save(payment);
        System.out.println("Payment status updated to SUCCESS for COD orderId: " + orderId);

        // Cập nhật trạng thái đơn hàng thành DELIVERED
        orderService.updateOrderStatus(orderId, OrderStatus.DELIVERED);
        System.out.println("Order status updated to DELIVERED for COD orderId: " + orderId);

        // Tạo giao dịch (nếu cần)
        transactionService.createTransaction(payment, null, "00", "");

        // Gửi email thông báo giao hàng thành công
        System.out.println("Preparing to send delivery email for COD orderId: " + orderId);
        InvoiceDTO invoiceDTO = mapToInvoiceDTO(orderId);
        String emailBody = buildDeliveryEmailBody(invoiceDTO);
        String recipientEmail = invoiceDTO.getEmail();
        String subject = String.format("Đơn hàng #%s đã được giao thành công - %s (%s)",
            orderId, invoiceDTO.getCustomerName(), java.time.LocalDate.now());
        emailService.sendEmail(recipientEmail, subject, emailBody);
        System.out.println("Delivery email sent to: " + recipientEmail + " for COD orderId: " + orderId);

        return updatedPayment;
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(String orderId) {
        // Tìm Payment
        Optional<Payment> paymentOpt = paymentRepository.findByOrderId(orderId);
        if (!paymentOpt.isPresent()) {
            throw new InvalidInputException("Payment not found for orderId: " + orderId);
        }
        Payment payment = paymentOpt.get();

        // Tìm Order
        Optional<Order> orderOpt = orderService.getOrderByOrderId(orderId);
        if (!orderOpt.isPresent()) {
            throw new InvalidInputException("Order not found for orderId: " + orderId);
        }
        Order order = orderOpt.get();

        // Kiểm tra trạng thái đơn hàng
        OrderStatus currentStatus = order.getStatus();
        if (currentStatus == OrderStatus.DELIVERED) {
            throw new InvalidInputException("Cannot cancel order with status DELIVERED for orderId: " + orderId);
        }
        if (currentStatus == OrderStatus.CANCELLED) {
            throw new InvalidInputException("Order is already CANCELLED for orderId: " + orderId);
        }

        // Cập nhật trạng thái đơn hàng thành CANCELLED
        orderService.updateOrderStatus(orderId, OrderStatus.CANCELLED);
        System.out.println("Order status updated to CANCELLED for orderId: " + orderId);

        // Hoàn số lượng vào kho
        try {
            System.out.println("Attempting to restock for orderId: " + orderId);
            restockAfterCancellation(order);
        } catch (Exception e) {
            System.out.println("Failed to restock for orderId: " + orderId + ", error: " + e.getMessage());
            throw new InvalidInputException("Failed to restock: " + e.getMessage());
        }

        // Gửi email thông báo hủy đơn hàng
        System.out.println("Preparing to send cancellation email for orderId: " + orderId);
        InvoiceDTO invoiceDTO = mapToInvoiceDTO(orderId);
        String emailBody = buildCancellationEmailBody(invoiceDTO);
        String recipientEmail = invoiceDTO.getEmail();
        String subject = String.format("Đơn hàng #%s đã bị hủy - %s (%s)",
            orderId, invoiceDTO.getCustomerName(), java.time.LocalDate.now());
        emailService.sendEmail(recipientEmail, subject, emailBody);
        System.out.println("Cancellation email sent to: " + recipientEmail + " for orderId: " + orderId);
    }

    @Transactional(rollbackFor = Exception.class)
    private void restockAfterCancellation(Order order) {
        List<OrderItem> orderItems = order.getItems();
        if (orderItems.isEmpty()) {
            System.out.println("No items to restock for orderId: " + order.getOrderId());
            return;
        }

        for (OrderItem orderItem : orderItems) {
            VariantSize variantSize = orderItem.getSize();
            Integer quantityToRestock = orderItem.getQuantity();

            System.out.println("Restocking for variant: " + orderItem.getVariant().getId() + ", size: " + variantSize.getSize()
                    + ", quantity: " + quantityToRestock);

            // Cộng lại số lượng vào kho
            variantSize.setQuantity(variantSize.getQuantity() + quantityToRestock);
            variantSizeRepository.save(variantSize);
        }

        System.out.println("Restock completed for orderId: " + order.getOrderId());
    }

    @Transactional(rollbackFor = Exception.class)
    private void deductStockAfterPayment(Payment payment) {
        Optional<Cart> cartOptional;
        if (payment.getUser() != null) {
            Long userId = payment.getUser().getId();
            cartOptional = cartRepository.findByUserId(userId);
            System.out.println(
                    "Searching cart by userId: " + userId + ", result: " + cartOptional.map(Cart::getId).orElse(null));
        } else if (payment.getCartToken() != null && !payment.getCartToken().isEmpty()) {
            cartOptional = cartRepository.findByCartToken(payment.getCartToken());
            System.out.println("Searching cart by cartToken: " + payment.getCartToken() + ", result: "
                    + cartOptional.map(Cart::getId).orElse(null));
        } else {
            System.out.println("No cart or user info for payment: " + payment.getOrderId());
            throw new InvalidInputException("No cart or user information found for payment: " + payment.getOrderId());
        }

        Cart cart = cartOptional.orElseThrow(() -> {
            System.out
                    .println("Cart not found for payment: " + payment.getOrderId() + ", cartOptional: " + cartOptional);
            return new InvalidInputException("Cart not found for payment: " + payment.getOrderId());
        });

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        if (cartItems.isEmpty()) {
            System.out.println("Cart is empty for payment: " + payment.getOrderId());
            return;
        }

        for (CartItem cartItem : cartItems) {
            ProductVariant variant = cartItem.getVariant();
            VariantSize variantSize = cartItem.getSize();
            Integer quantityToDeduct = cartItem.getQuantity();

            System.out.println("Deducting stock for variant: " + variant.getId() + ", size: " + variantSize.getSize()
                    + ", quantity: " + quantityToDeduct);
            if (variantSize.getQuantity() < quantityToDeduct) {
                throw new InvalidInputException(
                        "Insufficient stock for variant: " + variant.getId() + ", size: " + variantSize.getSize());
            }

            variantSize.setQuantity(variantSize.getQuantity() - quantityToDeduct);
            variantSizeRepository.save(variantSize);
        }

        System.out.println("Stock deduction completed for payment: " + payment.getOrderId());
    }

    @Transactional(rollbackFor = Exception.class)
    private void clearCartAfterPayment(Payment payment) {
        Optional<Cart> cartOptional;
        if (payment.getUser() != null) {
            Long userId = payment.getUser().getId();
            cartOptional = cartRepository.findByUserId(userId);
            System.out.println(
                    "Searching cart by userId: " + userId + ", result: " + cartOptional.map(Cart::getId).orElse(null));
        } else if (payment.getCartToken() != null && !payment.getCartToken().isEmpty()) {
            cartOptional = cartRepository.findByCartToken(payment.getCartToken());
            System.out.println("Searching cart by cartToken: " + payment.getCartToken() + ", result: "
                    + cartOptional.map(Cart::getId).orElse(null));
        } else {
            System.out.println("No cart or user info for payment: " + payment.getOrderId());
            return;
        }

        if (cartOptional.isPresent()) {
            Cart cart = cartOptional.get();
            List<CartItem> cartItems = cartItemRepository.findByCart(cart);
            System.out.println("Clearing cart for payment: " + payment.getOrderId() + ", cartId: " + cart.getId());
            cartItemRepository.deleteAll(cartItems);
            cartRepository.delete(cart);
        } else {
            System.out.println("No cart found to clear for payment: " + payment.getOrderId());
        }
    }

    public Optional<Payment> getPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId);
    }
}