
// package com.ecommerce.Ecommerce.service;

// import com.ecommerce.Ecommerce.exception.InvalidInputException;
// import com.ecommerce.Ecommerce.model.*;
// import com.ecommerce.Ecommerce.model.dto.CartDTO;
// import com.ecommerce.Ecommerce.model.dto.InvoiceDTO;
// import com.ecommerce.Ecommerce.model.dto.InvoiceDTO.AddressDTO;
// import com.ecommerce.Ecommerce.model.dto.InvoiceDTO.InvoiceItemDTO;
// import com.ecommerce.Ecommerce.repository.*;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.util.List;
// import java.util.Optional;
// import java.util.stream.Collectors;

// @Service
// public class OrderService {

//     @Autowired
//     private OrderRepository orderRepository;

//     @Autowired
//     private OrderItemRepository orderItemRepository;

//     @Autowired
//     private UserRepository userRepository;

//     @Autowired
//     private ProductRepository productRepository;

//     @Autowired
//     private ProductVariantRepository productVariantRepository;

//     @Autowired
//     private VariantSizeRepository variantSizeRepository;

//     @Autowired
//     private ShippingMethodRepository shippingMethodRepository;

//     @Autowired
//     private CouponService couponService;
//     public List<InvoiceDTO> getAllOrders() {
//         return orderRepository.findAll().stream()
//                 .map(this::mapToInvoiceDTO)
//                 .collect(Collectors.toList());
//     }

//     @Transactional(rollbackFor = Exception.class)
//     public Order createOrder(String orderId, Long userId, CartDTO cartDTO, Address shippingAddress, 
//                              String shippingMethodCode, String couponCode, String email, 
//                              String customerName, String phoneNumber) {
//         System.out.println("Creating Order for orderId: " + orderId);

//         if (orderRepository.findByOrderId(orderId).isPresent()) {
//             throw new InvalidInputException("Order already exists for orderId: " + orderId);
//         }

//         if (shippingAddress == null || shippingAddress.getStreet() == null || shippingAddress.getWard() == null ||
//             shippingAddress.getDistrict() == null || shippingAddress.getCity() == null || shippingAddress.getCountry() == null) {
//             throw new InvalidInputException("Shipping address is incomplete for orderId: " + orderId);
//         }

//         if (email == null || email.trim().isEmpty()) {
//             throw new InvalidInputException("Email is required for orderId: " + orderId);
//         }

//         if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
//             throw new InvalidInputException("Invalid email format for orderId: " + orderId);
//         }

//         if (cartDTO == null || cartDTO.getItems() == null || cartDTO.getItems().isEmpty()) {
//             throw new InvalidInputException("Cart is empty or invalid for orderId: " + orderId);
//         }

//         User user = null;
//         if (userId != null) {
//             user = userRepository.findById(userId)
//                     .orElseThrow(() -> new InvalidInputException("User not found with id: " + userId));
//         }

//         ShippingMethod shippingMethod = null;
//         if (shippingMethodCode != null && !shippingMethodCode.trim().isEmpty()) {
//             shippingMethod = shippingMethodRepository.findByCode(shippingMethodCode)
//                     .orElseThrow(() -> new InvalidInputException("Shipping method not found with code: " + shippingMethodCode));
//             if (shippingMethod.getStatus() != ShippingMethodStatus.ACTIVE) {
//                 throw new InvalidInputException("Shipping method is inactive: " + shippingMethodCode);
//             }
//         } else {
//             throw new InvalidInputException("Shipping method code is required");
//         }

//         double cartTotal = cartDTO.getTotalPrice();
//         double shippingFee = shippingMethod.getShippingFee();
//         double baseTotal = cartTotal + shippingFee;

//         double discountedTotal = baseTotal;
//         boolean couponApplied = false;
//         Coupon appliedCoupon = null;
//         if (couponCode != null && !couponCode.trim().isEmpty()) {
//             try {
//                 appliedCoupon = couponService.validateCoupon(couponCode.trim(), userId);
//                 int discountRate = appliedCoupon.getDiscountRate();

//                 boolean hasDiscountedProducts = cartDTO.getItems().stream()
//                         .anyMatch(item -> item.getDiscountPrice() > 0);

//                 if (hasDiscountedProducts && !appliedCoupon.isApplicableToDiscountedProducts()) {
//                     throw new InvalidInputException("Coupon " + couponCode + " cannot be applied to discounted products");
//                 }

//                 double discountAmount = (cartTotal * discountRate) / 100;
//                 discountedTotal = cartTotal - discountAmount + shippingFee;

//                 if (discountedTotal < 0) {
//                     discountedTotal = shippingFee;
//                 }
//                 couponApplied = true;

//                 couponService.addUserToCoupon(appliedCoupon, userId);
//             } catch (InvalidInputException e) {
//                 System.out.println("Coupon validation failed: " + e.getMessage());
//             }
//         }

//         Order order = new Order(orderId, user, cartDTO.getCartToken(), discountedTotal, shippingAddress, 
//                                 shippingMethod, email, customerName, phoneNumber);
//         order.setStatus(OrderStatus.PENDING);
//         order.setCouponApplied(couponApplied);

//         Order savedOrder = orderRepository.save(order);

//         for (CartDTO.CartItemDTO cartItemDTO : cartDTO.getItems()) {
//             Product product = productRepository.findById(cartItemDTO.getProductId())
//                     .orElseThrow(() -> new InvalidInputException("Product not found with id: " + cartItemDTO.getProductId()));
//             ProductVariant variant = productVariantRepository.findById(cartItemDTO.getVariant().getId())
//                     .orElseThrow(() -> new InvalidInputException("Variant not found with id: " + cartItemDTO.getVariant().getId()));
//             VariantSize size = variantSizeRepository.findById(cartItemDTO.getSizeId())
//                     .orElseThrow(() -> new InvalidInputException("Size not found with id: " + cartItemDTO.getSizeId()));

//             if (size.getQuantity() < cartItemDTO.getQuantity()) {
//                 throw new InvalidInputException("Insufficient stock for variant: " + cartItemDTO.getVariant().getId() + ", size: " + cartItemDTO.getSize());
//             }

//             double price = cartItemDTO.getDiscountPrice() > 0 ? cartItemDTO.getDiscountPrice() : cartItemDTO.getPrice();

//             OrderItem orderItem = new OrderItem(
//                 savedOrder,
//                 product,
//                 variant,
//                 size,
//                 cartItemDTO.getQuantity(),
//                 price
//             );
//             orderItemRepository.save(orderItem);
//         }

//         System.out.println("Order and OrderItems created successfully for orderId: " + orderId);
//         return savedOrder;
//     }

//     @Transactional(rollbackFor = Exception.class)
//     public InvoiceDTO updateOrderStatus(String orderId, OrderStatus status) {
//         Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
//         if (!orderOpt.isPresent()) {
//             throw new InvalidInputException("Order not found for orderId: " + orderId);
//         }

//         Order order = orderOpt.get();

//         // Kiểm tra trạng thái hợp lệ
//         if (status == OrderStatus.CONFIRMED && order.getStatus() != OrderStatus.PENDING) {
//             throw new InvalidInputException("Order can only be confirmed from PENDING status");
//         }
//         if (status == OrderStatus.SHIPPED && order.getStatus() != OrderStatus.CONFIRMED) {
//             throw new InvalidInputException("Order can only be shipped from CONFIRMED status");
//         }
//         if (status == OrderStatus.DELIVERED && order.getStatus() != OrderStatus.SHIPPED) {
//             throw new InvalidInputException("Order can only be delivered from SHIPPED status");
//         }
//         if (status == OrderStatus.CANCELLED && order.getStatus() == OrderStatus.DELIVERED) {
//             throw new InvalidInputException("Delivered order cannot be cancelled");
//         }

//         // Tăng usedCount khi status đổi thành CONFIRMED và coupon đã được áp dụng
//         if (status == OrderStatus.CONFIRMED && order.isCouponApplied()) {
//             double expectedTotalWithoutCoupon = order.getShippingMethod().getShippingFee() +
//                     order.getItems().stream()
//                             .mapToDouble(item -> item.getPrice() * item.getQuantity())
//                             .sum();
//             if (order.getTotalAmount() < expectedTotalWithoutCoupon) {
//                 List<Coupon> coupons = couponService.getAllCoupons().stream()
//                         .filter(coupon -> coupon.getStatus() == CouponStatus.ACTIVE)
//                         .filter(coupon -> {
//                             double discountAmount = (order.getItems().stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum() * coupon.getDiscountRate()) / 100;
//                             double expectedDiscountedTotal = order.getItems().stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum() + order.getShippingMethod().getShippingFee() - discountAmount;
//                             return Math.abs(expectedDiscountedTotal - order.getTotalAmount()) < 0.01;
//                         })
//                         .toList();
//                 if (!coupons.isEmpty()) {
//                     Coupon coupon = coupons.get(0);
//                     try {
//                         couponService.incrementUsedCount(coupon, null);
//                         System.out.println("Increased usedCount for coupon " + coupon.getCode() + " to " + coupon.getUsedCount());
//                     } catch (InvalidInputException e) {
//                         System.out.println("Failed to update usedCount: " + e.getMessage());
//                     }
//                 } else {
//                     System.out.println("No valid coupon found for orderId: " + orderId);
//                 }
//             }
//         }

//         order.setStatus(status);
//         Order updatedOrder = orderRepository.save(order);
//         return mapToInvoiceDTO(updatedOrder);
//     }

//     @Transactional(rollbackFor = Exception.class)
//     public Order linkPaymentToOrder(String orderId, Payment payment) {
//         Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
//         if (!orderOpt.isPresent()) {
//             throw new InvalidInputException("Order not found for orderId: " + orderId);
//         }

//         Order order = orderOpt.get();
//         order.setPayment(payment);

//         return orderRepository.save(order);
//     }

//     public Optional<Order> getOrderByOrderId(String orderId) {
//         return orderRepository.findByOrderId(orderId);
//     }

//     public List<InvoiceDTO> getOrdersByUserId(Long userId) {
//         return orderRepository.findByUserId(userId).stream()
//                 .map(this::mapToInvoiceDTO)
//                 .collect(Collectors.toList());
//     }

//     public List<InvoiceDTO> getOrdersByCartToken(String cartToken) {
//         if (cartToken == null || cartToken.trim().isEmpty()) {
//             throw new InvalidInputException("Cart token is required");
//         }
//         return orderRepository.findByCartToken(cartToken).stream()
//                 .map(this::mapToInvoiceDTO)
//                 .collect(Collectors.toList());
//     }

//     // Phương thức ánh xạ từ Order sang InvoiceDTO
//     public InvoiceDTO mapToInvoiceDTO(Order order) {
//         if (order == null) {
//             throw new InvalidInputException("Order is null");
//         }

//         Payment payment = order.getPayment();
//         PaymentMethod paymentMethod = (payment != null) ? payment.getPaymentMethod() : null;
//         PaymentStatus paymentStatus = (payment != null) ? payment.getStatus() : null;

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
//                 item.getVariant().getMainImage() // Thêm mainImage từ ProductVariant
//             );
//         }).collect(Collectors.toList());

//         String customerIdentifier = order.getCartToken() != null ? order.getCartToken() 
//                 : (order.getUser() != null ? order.getUser().getId().toString() : null);

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
//             paymentMethod,
//             paymentStatus,
//             order.getStatus(),
//             items
//         );

//         // Gán createdAt và updatedAt từ Order (do InvoiceDTO extends Auditable)
//         invoiceDTO.setCreatedAt(order.getCreatedAt());
//         invoiceDTO.setUpdatedAt(order.getUpdatedAt());

//         return invoiceDTO;
//     }
// }
// package com.ecommerce.Ecommerce.service;

// import com.ecommerce.Ecommerce.exception.InvalidInputException;
// import com.ecommerce.Ecommerce.model.*;
// import com.ecommerce.Ecommerce.model.dto.CartDTO;
// import com.ecommerce.Ecommerce.model.dto.InvoiceDTO;
// import com.ecommerce.Ecommerce.model.dto.InvoiceDTO.AddressDTO;
// import com.ecommerce.Ecommerce.model.dto.InvoiceDTO.InvoiceItemDTO;
// import com.ecommerce.Ecommerce.repository.*;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.util.List;
// import java.util.Optional;
// import java.util.stream.Collectors;

// @Service
// public class OrderService {

//     @Autowired
//     private OrderRepository orderRepository;

//     @Autowired
//     private OrderItemRepository orderItemRepository;

//     @Autowired
//     private UserRepository userRepository;

//     @Autowired
//     private ProductRepository productRepository;

//     @Autowired
//     private ProductVariantRepository productVariantRepository;

//     @Autowired
//     private VariantSizeRepository variantSizeRepository;

//     @Autowired
//     private ShippingMethodRepository shippingMethodRepository;

//     @Autowired
//     private CouponService couponService;
//     @Autowired
//     private PaymentRepository paymentRepository; // T

//     public List<InvoiceDTO> getAllOrders() {
//         return orderRepository.findAll().stream()
//                 .map(this::mapToInvoiceDTO)
//                 .collect(Collectors.toList());
//     }

//     @Transactional(rollbackFor = Exception.class)
//     public Order createOrder(String orderId, Long userId, CartDTO cartDTO, Address shippingAddress,
//             String shippingMethodCode, String couponCode, String email,
//             String customerName, String phoneNumber) {
//         System.out.println("Creating Order for orderId: " + orderId);

//         if (orderRepository.findByOrderId(orderId).isPresent()) {
//             throw new InvalidInputException("Order already exists for orderId: " + orderId);
//         }

//         if (shippingAddress == null || shippingAddress.getStreet() == null || shippingAddress.getWard() == null ||
//                 shippingAddress.getDistrict() == null || shippingAddress.getCity() == null
//                 || shippingAddress.getCountry() == null) {
//             throw new InvalidInputException("Shipping address is incomplete for orderId: " + orderId);
//         }

//         if (email == null || email.trim().isEmpty()) {
//             throw new InvalidInputException("Email is required for orderId: " + orderId);
//         }

//         if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
//             throw new InvalidInputException("Invalid email format for orderId: " + orderId);
//         }

//         if (cartDTO == null || cartDTO.getItems() == null || cartDTO.getItems().isEmpty()) {
//             throw new InvalidInputException("Cart is empty or invalid for orderId: " + orderId);
//         }

//         User user = null;
//         if (userId != null) {
//             user = userRepository.findById(userId)
//                     .orElseThrow(() -> new InvalidInputException("User not found with id: " + userId));
//         }

//         ShippingMethod shippingMethod = null;
//         if (shippingMethodCode != null && !shippingMethodCode.trim().isEmpty()) {
//             shippingMethod = shippingMethodRepository.findByCode(shippingMethodCode)
//                     .orElseThrow(() -> new InvalidInputException(
//                             "Shipping method not found with code: " + shippingMethodCode));
//             if (shippingMethod.getStatus() != ShippingMethodStatus.ACTIVE) {
//                 throw new InvalidInputException("Shipping method is inactive: " + shippingMethodCode);
//             }
//         } else {
//             throw new InvalidInputException("Shipping method code is required");
//         }

//         double cartTotal = cartDTO.getTotalPrice();
//         double shippingFee = shippingMethod.getShippingFee();
//         if (cartTotal > 1000000) {
//             shippingFee = 0;
//         }
//         double baseTotal = cartTotal + shippingFee;

//         double discountedTotal = baseTotal;
//         boolean couponApplied = false;
//         Coupon appliedCoupon = null;
//         if (couponCode != null && !couponCode.trim().isEmpty()) {
//             try {
//                 appliedCoupon = couponService.validateCoupon(couponCode.trim(), userId);
//                 int discountRate = appliedCoupon.getDiscountRate();
//                 Double maxDiscountAmount = appliedCoupon.getMaxDiscountAmount(); // Lấy giá trị giảm tối đa

//                 boolean hasDiscountedProducts = cartDTO.getItems().stream()
//                         .anyMatch(item -> item.getDiscountPrice() > 0);

//                 if (hasDiscountedProducts && !appliedCoupon.isApplicableToDiscountedProducts()) {
//                     throw new InvalidInputException(
//                             "Coupon " + couponCode + " cannot be applied to discounted products");
//                 }

//                 double discountAmount = (cartTotal * discountRate) / 100;

//                 // Áp dụng giới hạn giảm tối đa nếu có
//                 if (maxDiscountAmount != null && discountAmount > maxDiscountAmount) {
//                     discountAmount = maxDiscountAmount; // Giới hạn discountAmount
//                 }

//                 discountedTotal = cartTotal - discountAmount + shippingFee;

//                 if (discountedTotal < 0) {
//                     discountedTotal = shippingFee; // Đảm bảo không âm
//                 }
//                 couponApplied = true;

//                 couponService.addUserToCoupon(appliedCoupon, userId);
//             } catch (InvalidInputException e) {
//                 System.out.println("Coupon validation failed: " + e.getMessage());
//             }
//         }

//         Order order = new Order(orderId, user, cartDTO.getCartToken(), discountedTotal, shippingAddress,
//                 shippingMethod, email, customerName, phoneNumber);
//         order.setStatus(OrderStatus.PENDING);
//         order.setCouponApplied(couponApplied);

//         Order savedOrder = orderRepository.save(order);

//         for (CartDTO.CartItemDTO cartItemDTO : cartDTO.getItems()) {
//             Product product = productRepository.findById(cartItemDTO.getProductId())
//                     .orElseThrow(() -> new InvalidInputException(
//                             "Product not found with id: " + cartItemDTO.getProductId()));
//             ProductVariant variant = productVariantRepository.findById(cartItemDTO.getVariant().getId())
//                     .orElseThrow(() -> new InvalidInputException(
//                             "Variant not found with id: " + cartItemDTO.getVariant().getId()));
//             VariantSize size = variantSizeRepository.findById(cartItemDTO.getSizeId())
//                     .orElseThrow(() -> new InvalidInputException("Size not found with id: " + cartItemDTO.getSizeId()));

//             if (size.getQuantity() < cartItemDTO.getQuantity()) {
//                 throw new InvalidInputException("Insufficient stock for variant: " + cartItemDTO.getVariant().getId()
//                         + ", size: " + cartItemDTO.getSize());
//             }

//             double price = cartItemDTO.getDiscountPrice() > 0 ? cartItemDTO.getDiscountPrice() : cartItemDTO.getPrice();

//             OrderItem orderItem = new OrderItem(
//                     savedOrder,
//                     product,
//                     variant,
//                     size,
//                     cartItemDTO.getQuantity(),
//                     price);
//             orderItemRepository.save(orderItem);
//         }

//         System.out.println("Order and OrderItems created successfully for orderId: " + orderId);
//         return savedOrder;
//     }

//     @Transactional(rollbackFor = Exception.class)
//     public InvoiceDTO updateOrderStatus(String orderId, OrderStatus status) {
//         Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
//         if (!orderOpt.isPresent()) {
//             throw new InvalidInputException("Order not found for orderId: " + orderId);
//         }

//         Order order = orderOpt.get();

//         // Kiểm tra trạng thái hợp lệ
//         if (status == OrderStatus.CONFIRMED && order.getStatus() != OrderStatus.PENDING) {
//             throw new InvalidInputException("Order can only be confirmed from PENDING status");
//         }
//         if (status == OrderStatus.SHIPPED && order.getStatus() != OrderStatus.CONFIRMED) {
//             throw new InvalidInputException("Order can only be shipped from CONFIRMED status");
//         }
//         if (status == OrderStatus.DELIVERED && order.getStatus() != OrderStatus.SHIPPED) {
//             throw new InvalidInputException("Order can only be delivered from SHIPPED status");
//         }
//         if (status == OrderStatus.CANCELLED && order.getStatus() == OrderStatus.DELIVERED) {
//             throw new InvalidInputException("Delivered order cannot be cancelled");
//         }

//         // Tăng usedCount khi status đổi thành CONFIRMED và coupon đã được áp dụng
//         if (status == OrderStatus.CONFIRMED && order.isCouponApplied()) {
//             double expectedTotalWithoutCoupon = order.getShippingMethod().getShippingFee() +
//                     order.getItems().stream()
//                             .mapToDouble(item -> item.getPrice() * item.getQuantity())
//                             .sum();
//             if (order.getTotalAmount() < expectedTotalWithoutCoupon) {
//                 List<Coupon> coupons = couponService.getAllCoupons().stream()
//                         .filter(coupon -> coupon.getStatus() == CouponStatus.ACTIVE)
//                         .filter(coupon -> {
//                             double discountAmount = (order.getItems().stream()
//                                     .mapToDouble(item -> item.getPrice() * item.getQuantity()).sum()
//                                     * coupon.getDiscountRate()) / 100;
//                             // Áp dụng maxDiscountAmount trong kiểm tra
//                             Double maxDiscountAmount = coupon.getMaxDiscountAmount();
//                             if (maxDiscountAmount != null && discountAmount > maxDiscountAmount) {
//                                 discountAmount = maxDiscountAmount;
//                             }
//                             double expectedDiscountedTotal = order.getItems().stream()
//                                     .mapToDouble(item -> item.getPrice() * item.getQuantity()).sum()
//                                     + order.getShippingMethod().getShippingFee() - discountAmount;
//                             return Math.abs(expectedDiscountedTotal - order.getTotalAmount()) < 0.01;
//                         })
//                         .toList();
//                 if (!coupons.isEmpty()) {
//                     Coupon coupon = coupons.get(0);
//                     try {
//                         couponService.incrementUsedCount(coupon, null);
//                         System.out.println(
//                                 "Increased usedCount for coupon " + coupon.getCode() + " to " + coupon.getUsedCount());
//                     } catch (InvalidInputException e) {
//                         System.out.println("Failed to update usedCount: " + e.getMessage());
//                     }
//                 } else {
//                     System.out.println("No valid coupon found for orderId: " + orderId);
//                 }
//             }
//         }

//         order.setStatus(status);
//         if (status == OrderStatus.DELIVERED && order.getPayment() != null) {
//             Payment payment = order.getPayment();
//             if (payment.getStatus() == PaymentStatus.PENDING) {
//                 payment.setStatus(PaymentStatus.SUCCESS);
//                 paymentRepository.save(payment); // Lưu thay đổi trạng thái thanh toán
//                 System.out.println("Payment status updated to SUCCESS for orderId: " + orderId);
//             }
//         }
//         Order updatedOrder = orderRepository.save(order);
//         return mapToInvoiceDTO(updatedOrder);
//     }

//     @Transactional(rollbackFor = Exception.class)
//     public Order linkPaymentToOrder(String orderId, Payment payment) {
//         Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
//         if (!orderOpt.isPresent()) {
//             throw new InvalidInputException("Order not found for orderId: " + orderId);
//         }

//         Order order = orderOpt.get();
//         order.setPayment(payment);

//         return orderRepository.save(order);
//     }

//     public Optional<Order> getOrderByOrderId(String orderId) {
//         return orderRepository.findByOrderId(orderId);
//     }

//     public List<InvoiceDTO> getOrdersByUserId(Long userId) {
//         return orderRepository.findByUserId(userId).stream()
//                 .map(this::mapToInvoiceDTO)
//                 .collect(Collectors.toList());
//     }

//     public List<InvoiceDTO> getOrdersByCartToken(String cartToken) {
//         if (cartToken == null || cartToken.trim().isEmpty()) {
//             throw new InvalidInputException("Cart token is required");
//         }
//         return orderRepository.findByCartToken(cartToken).stream()
//                 .map(this::mapToInvoiceDTO)
//                 .collect(Collectors.toList());
//     }

//    // Phương thức ánh xạ từ Order sang InvoiceDTO
//    public InvoiceDTO mapToInvoiceDTO(Order order) {
//     if (order == null) {
//         throw new InvalidInputException("Order is null");
//     }

//     Payment payment = order.getPayment();
//     PaymentMethod paymentMethod = (payment != null) ? payment.getPaymentMethod() : null;
//     PaymentStatus paymentStatus = (payment != null) ? payment.getStatus() : null;
//     String transactionId = (payment != null) ? payment.getTransactionId() : null; // Lấy transactionId từ Payment

//     AddressDTO shippingAddressDTO = new AddressDTO(
//             order.getShippingAddress().getStreet(),
//             order.getShippingAddress().getWard(),
//             order.getShippingAddress().getDistrict(),
//             order.getShippingAddress().getCity(),
//             order.getShippingAddress().getCountry());

//     List<InvoiceItemDTO> items = order.getItems().stream().map(item -> {
//         return new InvoiceItemDTO(
//                 item.getProduct().getId(),
//                 item.getProduct().getName(),
//                 item.getVariant().getColor(),
//                 item.getSize().getSize(),
//                 item.getQuantity(),
//                 item.getPrice(),
//                 item.getVariant().getMainImage()
//         );
//     }).collect(Collectors.toList());

//     String customerIdentifier = order.getCartToken() != null ? order.getCartToken()
//             : (order.getUser() != null ? order.getUser().getId().toString() : null);

//     InvoiceDTO invoiceDTO = new InvoiceDTO(
//             order.getOrderId(),
//             customerIdentifier,
//             order.getCustomerName(),
//             order.getEmail(),
//             order.getPhoneNumber(),
//             shippingAddressDTO,
//             order.getShippingMethod().getName(),
//             order.getTotalAmount(),
//             order.getShippingMethod().getShippingFee(),
//             paymentMethod,
//             paymentStatus,
//             order.getStatus(),
//             transactionId, // Gán transactionId vào InvoiceDTO
//             items);

//     invoiceDTO.setCreatedAt(order.getCreatedAt());
//     invoiceDTO.setUpdatedAt(order.getUpdatedAt());

//     return invoiceDTO;
// }

//     // tra cứu đơn hàng
//     public List<InvoiceDTO> getOrdersByPhoneNumber(String phoneNumber) {
//         if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
//             throw new InvalidInputException("Phone number is required");
//         }
//         return orderRepository.findByPhoneNumber(phoneNumber).stream()
//                 .map(this::mapToInvoiceDTO)
//                 .collect(Collectors.toList());
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service xử lý các logic liên quan đến đơn hàng (Order).
 * Quản lý việc tạo, cập nhật trạng thái, và truy xuất đơn hàng.
 */
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private VariantSizeRepository variantSizeRepository;

    @Autowired
    private ShippingMethodRepository shippingMethodRepository;

    @Autowired
    private CouponService couponService;

    @Autowired
    private PaymentRepository paymentRepository;

    /**
     * Lấy danh sách tất cả đơn hàng.
     * @return Danh sách các đơn hàng dưới dạng InvoiceDTO, sắp xếp theo ID giảm dần.
     */
    public List<InvoiceDTO> getAllOrders() {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return orderRepository.findAll(sort).stream()
                .map(this::mapToInvoiceDTO)
                .collect(Collectors.toList());
    }

    /**
     * Tạo một đơn hàng mới.
     * - Kiểm tra tính hợp lệ của orderId, địa chỉ giao hàng, email, và giỏ hàng.
     * - Áp dụng mã giảm giá (nếu có) và tính toán tổng tiền.
     * - Tạo Order và các OrderItem liên quan.
     * @param orderId Mã đơn hàng.
     * @param userId ID của người dùng (có thể null nếu là khách).
     * @param cartDTO Thông tin giỏ hàng.
     * @param shippingAddress Địa chỉ giao hàng.
     * @param shippingMethodCode Mã phương thức vận chuyển.
     * @param couponCode Mã giảm giá (có thể null).
     * @param email Email của khách hàng.
     * @param customerName Tên khách hàng.
     * @param phoneNumber Số điện thoại khách hàng.
     * @return Đơn hàng đã được tạo.
     * @throws InvalidInputException Nếu dữ liệu đầu vào không hợp lệ.
     */
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(String orderId, Long userId, CartDTO cartDTO, Address shippingAddress,
            String shippingMethodCode, String couponCode, String email,
            String customerName, String phoneNumber) {
        System.out.println("Creating Order for orderId: " + orderId);

        if (orderRepository.findByOrderId(orderId).isPresent()) {
            throw new InvalidInputException("Order already exists for orderId: " + orderId);
        }

        if (shippingAddress == null || shippingAddress.getStreet() == null || shippingAddress.getWard() == null ||
                shippingAddress.getDistrict() == null || shippingAddress.getCity() == null
                || shippingAddress.getCountry() == null) {
            throw new InvalidInputException("Shipping address is incomplete for orderId: " + orderId);
        }

        if (email == null || email.trim().isEmpty()) {
            throw new InvalidInputException("Email is required for orderId: " + orderId);
        }

        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new InvalidInputException("Invalid email format for orderId: " + orderId);
        }

        if (cartDTO == null || cartDTO.getItems() == null || cartDTO.getItems().isEmpty()) {
            throw new InvalidInputException("Cart is empty or invalid for orderId: " + orderId);
        }

        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new InvalidInputException("User not found with id: " + userId));
        }

        ShippingMethod shippingMethod = null;
        if (shippingMethodCode != null && !shippingMethodCode.trim().isEmpty()) {
            shippingMethod = shippingMethodRepository.findByCode(shippingMethodCode)
                    .orElseThrow(() -> new InvalidInputException(
                            "Shipping method not found with code: " + shippingMethodCode));
            if (shippingMethod.getStatus() != ShippingMethodStatus.ACTIVE) {
                throw new InvalidInputException("Shipping method is inactive: " + shippingMethodCode);
            }
        } else {
            throw new InvalidInputException("Shipping method code is required");
        }

        double cartTotal = cartDTO.getTotalPrice();
        double shippingFee = shippingMethod.getShippingFee();
        if (cartTotal > 1000000) {
            shippingFee = 0;
        }
        double baseTotal = cartTotal + shippingFee;

        double discountedTotal = baseTotal;
        boolean couponApplied = false;
        Coupon appliedCoupon = null;
        if (couponCode != null && !couponCode.trim().isEmpty()) {
            try {
                appliedCoupon = couponService.validateCoupon(couponCode.trim(), userId);
                int discountRate = appliedCoupon.getDiscountRate();
                Double maxDiscountAmount = appliedCoupon.getMaxDiscountAmount(); // Lấy giá trị giảm tối đa

                boolean hasDiscountedProducts = cartDTO.getItems().stream()
                        .anyMatch(item -> item.getDiscountPrice() > 0);

                if (hasDiscountedProducts && !appliedCoupon.isApplicableToDiscountedProducts()) {
                    throw new InvalidInputException(
                            "Coupon " + couponCode + " cannot be applied to discounted products");
                }

                double discountAmount = (cartTotal * discountRate) / 100;

                // Áp dụng giới hạn giảm tối đa nếu có
                if (maxDiscountAmount != null && discountAmount > maxDiscountAmount) {
                    discountAmount = maxDiscountAmount; // Giới hạn discountAmount
                }

                discountedTotal = cartTotal - discountAmount + shippingFee;

                if (discountedTotal < 0) {
                    discountedTotal = shippingFee; // Đảm bảo không âm
                }
                couponApplied = true;

                couponService.addUserToCoupon(appliedCoupon, userId);
            } catch (InvalidInputException e) {
                System.out.println("Coupon validation failed: " + e.getMessage());
            }
        }

        Order order = new Order(orderId, user, cartDTO.getCartToken(), discountedTotal, shippingAddress,
                shippingMethod, email, customerName, phoneNumber);
        order.setStatus(OrderStatus.PENDING);
        order.setCouponApplied(couponApplied);

        Order savedOrder = orderRepository.save(order);

        for (CartDTO.CartItemDTO cartItemDTO : cartDTO.getItems()) {
            Product product = productRepository.findById(cartItemDTO.getProductId())
                    .orElseThrow(() -> new InvalidInputException(
                            "Product not found with id: " + cartItemDTO.getProductId()));
            ProductVariant variant = productVariantRepository.findById(cartItemDTO.getVariant().getId())
                    .orElseThrow(() -> new InvalidInputException(
                            "Variant not found with id: " + cartItemDTO.getVariant().getId()));
            VariantSize size = variantSizeRepository.findById(cartItemDTO.getSizeId())
                    .orElseThrow(() -> new InvalidInputException("Size not found with id: " + cartItemDTO.getSizeId()));

            if (size.getQuantity() < cartItemDTO.getQuantity()) {
                throw new InvalidInputException("Insufficient stock for variant: " + cartItemDTO.getVariant().getId()
                        + ", size: " + cartItemDTO.getSize());
            }

            double price = cartItemDTO.getDiscountPrice() > 0 ? cartItemDTO.getDiscountPrice() : cartItemDTO.getPrice();

            OrderItem orderItem = new OrderItem(
                    savedOrder,
                    product,
                    variant,
                    size,
                    cartItemDTO.getQuantity(),
                    price);
            orderItemRepository.save(orderItem);
        }

        System.out.println("Order and OrderItems created successfully for orderId: " + orderId);
        return savedOrder;
    }

    /**
     * Cập nhật trạng thái của một đơn hàng.
     * - Kiểm tra trạng thái hợp lệ trước khi cập nhật.
     * - Tăng usedCount của coupon khi đơn hàng được xác nhận (CONFIRMED).
     * - Cập nhật trạng thái thanh toán thành SUCCESS nếu đơn hàng được giao (DELIVERED).
     * @param orderId Mã đơn hàng.
     * @param status Trạng thái mới của đơn hàng.
     * @return InvoiceDTO chứa thông tin đơn hàng đã cập nhật.
     * @throws InvalidInputException Nếu trạng thái không hợp lệ hoặc đơn hàng không tồn tại.
     */
    @Transactional(rollbackFor = Exception.class)
    public InvoiceDTO updateOrderStatus(String orderId, OrderStatus status) {
        Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
        if (!orderOpt.isPresent()) {
            throw new InvalidInputException("Order not found for orderId: " + orderId);
        }

        Order order = orderOpt.get();

        // Kiểm tra trạng thái hợp lệ
        if (status == OrderStatus.CONFIRMED && order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidInputException("Order can only be confirmed from PENDING status");
        }
        if (status == OrderStatus.SHIPPED && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new InvalidInputException("Order can only be shipped from CONFIRMED status");
        }
        if (status == OrderStatus.DELIVERED && order.getStatus() != OrderStatus.SHIPPED) {
            throw new InvalidInputException("Order can only be delivered from SHIPPED status");
        }
        if (status == OrderStatus.CANCELLED && order.getStatus() == OrderStatus.DELIVERED) {
            throw new InvalidInputException("Delivered order cannot be cancelled");
        }

        // Tăng usedCount khi status đổi thành CONFIRMED và coupon đã được áp dụng
        if (status == OrderStatus.CONFIRMED && order.isCouponApplied()) {
            double expectedTotalWithoutCoupon = order.getShippingMethod().getShippingFee() +
                    order.getItems().stream()
                            .mapToDouble(item -> item.getPrice() * item.getQuantity())
                            .sum();
            if (order.getTotalAmount() < expectedTotalWithoutCoupon) {
                List<Coupon> coupons = couponService.getAllCoupons().stream()
                        .filter(coupon -> coupon.getStatus() == CouponStatus.ACTIVE)
                        .filter(coupon -> {
                            double discountAmount = (order.getItems().stream()
                                    .mapToDouble(item -> item.getPrice() * item.getQuantity()).sum()
                                    * coupon.getDiscountRate()) / 100;
                            // Áp dụng maxDiscountAmount trong kiểm tra
                            Double maxDiscountAmount = coupon.getMaxDiscountAmount();
                            if (maxDiscountAmount != null && discountAmount > maxDiscountAmount) {
                                discountAmount = maxDiscountAmount;
                            }
                            double expectedDiscountedTotal = order.getItems().stream()
                                    .mapToDouble(item -> item.getPrice() * item.getQuantity()).sum()
                                    + order.getShippingMethod().getShippingFee() - discountAmount;
                            return Math.abs(expectedDiscountedTotal - order.getTotalAmount()) < 0.01;
                        })
                        .toList();
                if (!coupons.isEmpty()) {
                    Coupon coupon = coupons.get(0);
                    try {
                        couponService.incrementUsedCount(coupon, null);
                        System.out.println(
                                "Increased usedCount for coupon " + coupon.getCode() + " to " + coupon.getUsedCount());
                    } catch (InvalidInputException e) {
                        System.out.println("Failed to update usedCount: " + e.getMessage());
                    }
                } else {
                    System.out.println("No valid coupon found for orderId: " + orderId);
                }
            }
        }

        order.setStatus(status);
        if (status == OrderStatus.DELIVERED && order.getPayment() != null) {
            Payment payment = order.getPayment();
            if (payment.getStatus() == PaymentStatus.PENDING) {
                payment.setStatus(PaymentStatus.SUCCESS);
                paymentRepository.save(payment); // Lưu thay đổi trạng thái thanh toán
                System.out.println("Payment status updated to SUCCESS for orderId: " + orderId);
            }
        }
        Order updatedOrder = orderRepository.save(order);
        return mapToInvoiceDTO(updatedOrder);
    }

    /**
     * Liên kết một thanh toán với đơn hàng.
     * @param orderId Mã đơn hàng.
     * @param payment Thông tin thanh toán.
     * @return Đơn hàng đã được cập nhật với thông tin thanh toán.
     * @throws InvalidInputException Nếu đơn hàng không tồn tại.
     */
    @Transactional(rollbackFor = Exception.class)
    public Order linkPaymentToOrder(String orderId, Payment payment) {
        Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
        if (!orderOpt.isPresent()) {
            throw new InvalidInputException("Order not found for orderId: " + orderId);
        }

        Order order = orderOpt.get();
        order.setPayment(payment);

        return orderRepository.save(order);
    }

    /**
     * Lấy thông tin đơn hàng theo mã đơn hàng.
     * @param orderId Mã đơn hàng.
     * @return Optional chứa đơn hàng nếu tìm thấy, hoặc empty nếu không tìm thấy.
     */
    public Optional<Order> getOrderByOrderId(String orderId) {
        return orderRepository.findByOrderId(orderId);
    }

    /**
     * Lấy danh sách đơn hàng của một người dùng.
     * @param userId ID của người dùng.
     * @return Danh sách đơn hàng dưới dạng InvoiceDTO, sắp xếp theo ID giảm dần.
     */
    public List<InvoiceDTO> getOrdersByUserId(Long userId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return orderRepository.findByUserId(userId, sort).stream()
                .map(this::mapToInvoiceDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách đơn hàng theo mã giỏ hàng (cart token).
     * @param cartToken Mã giỏ hàng.
     * @return Danh sách đơn hàng dưới dạng InvoiceDTO, sắp xếp theo ID giảm dần.
     * @throws InvalidInputException Nếu cart token không hợp lệ.
     */
    public List<InvoiceDTO> getOrdersByCartToken(String cartToken) {
        if (cartToken == null || cartToken.trim().isEmpty()) {
            throw new InvalidInputException("Cart token is required");
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return orderRepository.findByCartToken(cartToken, sort).stream()
                .map(this::mapToInvoiceDTO)
                .collect(Collectors.toList());
    }

    /**
     * Ánh xạ một đơn hàng sang InvoiceDTO để trả về cho client.
     * @param order Đơn hàng cần ánh xạ.
     * @return InvoiceDTO chứa thông tin đơn hàng.
     * @throws InvalidInputException Nếu đơn hàng là null.
     */
    public InvoiceDTO mapToInvoiceDTO(Order order) {
        if (order == null) {
            throw new InvalidInputException("Order is null");
        }

        Payment payment = order.getPayment();
        PaymentMethod paymentMethod = (payment != null) ? payment.getPaymentMethod() : null;
        PaymentStatus paymentStatus = (payment != null) ? payment.getStatus() : null;
        String transactionId = (payment != null) ? payment.getTransactionId() : null;

        AddressDTO shippingAddressDTO = new AddressDTO(
                order.getShippingAddress().getStreet(),
                order.getShippingAddress().getWard(),
                order.getShippingAddress().getDistrict(),
                order.getShippingAddress().getCity(),
                order.getShippingAddress().getCountry());

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

        String customerIdentifier = order.getCartToken() != null ? order.getCartToken()
                : (order.getUser() != null ? order.getUser().getId().toString() : null);

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
                paymentMethod,
                paymentStatus,
                order.getStatus(),
                transactionId,
                items);

        invoiceDTO.setCreatedAt(order.getCreatedAt());
        invoiceDTO.setUpdatedAt(order.getUpdatedAt());

        return invoiceDTO;
    }

    /**
     * Tra cứu đơn hàng theo số điện thoại.
     * @param phoneNumber Số điện thoại của khách hàng.
     * @return Danh sách đơn hàng dưới dạng InvoiceDTO, sắp xếp theo ID giảm dần.
     * @throws InvalidInputException Nếu số điện thoại không hợp lệ.
     */
    public List<InvoiceDTO> getOrdersByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new InvalidInputException("Phone number is required");
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return orderRepository.findByPhoneNumber(phoneNumber, sort).stream()
                .map(this::mapToInvoiceDTO)
                .collect(Collectors.toList());
    }
}