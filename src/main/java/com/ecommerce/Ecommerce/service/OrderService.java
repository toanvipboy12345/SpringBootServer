
// package com.ecommerce.Ecommerce.service;

// import com.ecommerce.Ecommerce.exception.InvalidInputException;
// import com.ecommerce.Ecommerce.model.*;
// import com.ecommerce.Ecommerce.model.dto.CartDTO;
// import com.ecommerce.Ecommerce.repository.*;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.util.List;
// import java.util.Optional;

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


// @Transactional(rollbackFor = Exception.class)
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
//                 appliedCoupon = couponService.validateCoupon(couponCode.trim(), userId); // Truyền userId
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
               
//             } catch (InvalidInputException e) {
//                 System.out.println("Coupon validation failed: " + e.getMessage());
//             }
//         }

//         // Tạo Order với cartToken từ CartDTO
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
// public Order updateOrderStatus(String orderId, OrderStatus status) {
//     Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
//     if (!orderOpt.isPresent()) {
//         throw new InvalidInputException("Order not found for orderId: " + orderId);
//     }

//     Order order = orderOpt.get();

//     // Tăng usedCount khi status thay đổi thành CONFIRMED và coupon đã được áp dụng
//     if (status == OrderStatus.CONFIRMED && order.isCouponApplied()) {
//         double expectedTotalWithoutCoupon = order.getShippingMethod().getShippingFee() +
//                 order.getItems().stream()
//                         .mapToDouble(item -> item.getPrice() * item.getQuantity())
//                         .sum();
//         if (order.getTotalAmount() < expectedTotalWithoutCoupon) { // Xác nhận coupon đã được áp dụng
//             // Tìm couponCode dựa trên totalAmount (lấy coupon đầu tiên khớp với discountRate)
//             List<Coupon> coupons = couponService.getAllCoupons().stream()
//                     .filter(coupon -> coupon.getStatus() == CouponStatus.ACTIVE)
//                     .filter(coupon -> {
//                         double discountAmount = (order.getItems().stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum() * coupon.getDiscountRate()) / 100;
//                         double expectedDiscountedTotal = order.getItems().stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum() + order.getShippingMethod().getShippingFee() - discountAmount;
//                         return Math.abs(expectedDiscountedTotal - order.getTotalAmount()) < 0.01; // So sánh với sai số nhỏ
//                     })
//                     .toList();
//             if (!coupons.isEmpty()) {
//                 Coupon coupon = coupons.get(0); // Lấy coupon đầu tiên khớp (giả định chỉ có một coupon hợp lệ)
//                 try {
//                     if (coupon.getUsedCount() < coupon.getMaxUses()) {
//                         coupon.setUsedCount(coupon.getUsedCount() + 1);
//                         couponService.updateCoupon(coupon.getId(), coupon);
//                         System.out.println("Increased usedCount for coupon " + coupon.getCode() + " to " + coupon.getUsedCount());
//                     } else {
//                         throw new InvalidInputException("Coupon " + coupon.getCode() + " has reached maximum uses");
//                     }
//                 } catch (InvalidInputException e) {
//                     System.out.println("Failed to update usedCount: " + e.getMessage());
//                     // Không throw exception, chỉ log lỗi
//                 }
//             } else {
//                 System.out.println("No valid coupon found for orderId: " + orderId);
//             }
//         }
//     }

//     order.setStatus(status);
//     return orderRepository.save(order);
// }

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

//     public List<Order> getOrdersByUserId(Long userId) {
//         return orderRepository.findByUserId(userId);
//     }
// }
package com.ecommerce.Ecommerce.service;

import com.ecommerce.Ecommerce.exception.InvalidInputException;
import com.ecommerce.Ecommerce.model.*;
import com.ecommerce.Ecommerce.model.dto.CartDTO;
import com.ecommerce.Ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(String orderId, Long userId, CartDTO cartDTO, Address shippingAddress, 
                             String shippingMethodCode, String couponCode, String email, 
                             String customerName, String phoneNumber) {
        System.out.println("Creating Order for orderId: " + orderId);

        if (orderRepository.findByOrderId(orderId).isPresent()) {
            throw new InvalidInputException("Order already exists for orderId: " + orderId);
        }

        if (shippingAddress == null || shippingAddress.getStreet() == null || shippingAddress.getWard() == null ||
            shippingAddress.getDistrict() == null || shippingAddress.getCity() == null || shippingAddress.getCountry() == null) {
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
                    .orElseThrow(() -> new InvalidInputException("Shipping method not found with code: " + shippingMethodCode));
            if (shippingMethod.getStatus() != ShippingMethodStatus.ACTIVE) {
                throw new InvalidInputException("Shipping method is inactive: " + shippingMethodCode);
            }
        } else {
            throw new InvalidInputException("Shipping method code is required");
        }

        double cartTotal = cartDTO.getTotalPrice();
        double shippingFee = shippingMethod.getShippingFee();
        double baseTotal = cartTotal + shippingFee;

        double discountedTotal = baseTotal;
        boolean couponApplied = false;
        Coupon appliedCoupon = null;
        if (couponCode != null && !couponCode.trim().isEmpty()) {
            try {
                appliedCoupon = couponService.validateCoupon(couponCode.trim(), userId); // Truyền userId
                int discountRate = appliedCoupon.getDiscountRate();

                boolean hasDiscountedProducts = cartDTO.getItems().stream()
                        .anyMatch(item -> item.getDiscountPrice() > 0);

                if (hasDiscountedProducts && !appliedCoupon.isApplicableToDiscountedProducts()) {
                    throw new InvalidInputException("Coupon " + couponCode + " cannot be applied to discounted products");
                }

                double discountAmount = (cartTotal * discountRate) / 100;
                discountedTotal = cartTotal - discountAmount + shippingFee;

                if (discountedTotal < 0) {
                    discountedTotal = shippingFee;
                }
                couponApplied = true;

                // Chỉ thêm userId vào danh sách usedByUsers, không tăng usedCount ở đây
                couponService.addUserToCoupon(appliedCoupon, userId);
            } catch (InvalidInputException e) {
                System.out.println("Coupon validation failed: " + e.getMessage());
            }
        }

        // Tạo Order với cartToken từ CartDTO
        Order order = new Order(orderId, user, cartDTO.getCartToken(), discountedTotal, shippingAddress, 
                                shippingMethod, email, customerName, phoneNumber);
        order.setStatus(OrderStatus.PENDING);
        order.setCouponApplied(couponApplied);

        Order savedOrder = orderRepository.save(order);

        for (CartDTO.CartItemDTO cartItemDTO : cartDTO.getItems()) {
            Product product = productRepository.findById(cartItemDTO.getProductId())
                    .orElseThrow(() -> new InvalidInputException("Product not found with id: " + cartItemDTO.getProductId()));
            ProductVariant variant = productVariantRepository.findById(cartItemDTO.getVariant().getId())
                    .orElseThrow(() -> new InvalidInputException("Variant not found with id: " + cartItemDTO.getVariant().getId()));
            VariantSize size = variantSizeRepository.findById(cartItemDTO.getSizeId())
                    .orElseThrow(() -> new InvalidInputException("Size not found with id: " + cartItemDTO.getSizeId()));

            if (size.getQuantity() < cartItemDTO.getQuantity()) {
                throw new InvalidInputException("Insufficient stock for variant: " + cartItemDTO.getVariant().getId() + ", size: " + cartItemDTO.getSize());
            }

            double price = cartItemDTO.getDiscountPrice() > 0 ? cartItemDTO.getDiscountPrice() : cartItemDTO.getPrice();

            OrderItem orderItem = new OrderItem(
                savedOrder,
                product,
                variant,
                size,
                cartItemDTO.getQuantity(),
                price
            );
            orderItemRepository.save(orderItem);
        }

        System.out.println("Order and OrderItems created successfully for orderId: " + orderId);
        return savedOrder;
    }

    @Transactional(rollbackFor = Exception.class)
    public Order updateOrderStatus(String orderId, OrderStatus status) {
        Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
        if (!orderOpt.isPresent()) {
            throw new InvalidInputException("Order not found for orderId: " + orderId);
        }

        Order order = orderOpt.get();

        // Tăng usedCount khi status đổi thành CONFIRMED và coupon đã được áp dụng
        if (status == OrderStatus.CONFIRMED && order.isCouponApplied()) {
            double expectedTotalWithoutCoupon = order.getShippingMethod().getShippingFee() +
                    order.getItems().stream()
                            .mapToDouble(item -> item.getPrice() * item.getQuantity())
                            .sum();
            if (order.getTotalAmount() < expectedTotalWithoutCoupon) { // Xác nhận coupon đã được áp dụng
                // Tìm coupon dựa trên totalAmount
                List<Coupon> coupons = couponService.getAllCoupons().stream()
                        .filter(coupon -> coupon.getStatus() == CouponStatus.ACTIVE)
                        .filter(coupon -> {
                            double discountAmount = (order.getItems().stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum() * coupon.getDiscountRate()) / 100;
                            double expectedDiscountedTotal = order.getItems().stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum() + order.getShippingMethod().getShippingFee() - discountAmount;
                            return Math.abs(expectedDiscountedTotal - order.getTotalAmount()) < 0.01; // So sánh với sai số nhỏ
                        })
                        .toList();
                if (!coupons.isEmpty()) {
                    Coupon coupon = coupons.get(0); // Lấy coupon đầu tiên khớp
                    try {
                        // Tăng usedCount (không cần thêm userId vì đã thêm trong createOrder)
                        couponService.incrementUsedCount(coupon, null); // Truyền null để không thêm userId lần nữa
                        System.out.println("Increased usedCount for coupon " + coupon.getCode() + " to " + coupon.getUsedCount());
                    } catch (InvalidInputException e) {
                        System.out.println("Failed to update usedCount: " + e.getMessage());
                    }
                } else {
                    System.out.println("No valid coupon found for orderId: " + orderId);
                }
            }
        }

        order.setStatus(status);
        return orderRepository.save(order);
    }

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

    public Optional<Order> getOrderByOrderId(String orderId) {
        return orderRepository.findByOrderId(orderId);
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}