
package com.ecommerce.Ecommerce.controller;

import com.ecommerce.Ecommerce.exception.InvalidInputException;
import com.ecommerce.Ecommerce.model.Address;
import com.ecommerce.Ecommerce.model.Payment;
import com.ecommerce.Ecommerce.model.PaymentMethod;
import com.ecommerce.Ecommerce.model.PaymentStatus;
import com.ecommerce.Ecommerce.model.dto.CartDTO;
import com.ecommerce.Ecommerce.service.PaymentService;
import com.ecommerce.Ecommerce.utils.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private VNPayUtil vnpayUtil;

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createPayment(@RequestBody PaymentRequest request) {
        Payment payment = paymentService.createPayment(
            request.getOrderId(),
            request.getUserId(),
            request.getCartDTO(),
            request.getPaymentMethod(),
            request.getShippingAddress()
        );
        Map<String, String> response = new HashMap<>();
        response.put("orderId", payment.getOrderId());
        response.put("paymentUrl", payment.getRequestData());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vnpay-payment-return")
    public ResponseEntity<Void> handlePaymentCallback(
            @RequestParam Map<String, String> allParams,
            HttpServletRequest request) {
        System.out.println("Incoming request to /vnpay-payment-return from IP: " + request.getRemoteAddr());
        System.out.println("Full request headers: " + Collections.list(request.getHeaderNames()).stream()
                .collect(Collectors.toMap(h -> h, request::getHeader)));
        System.out.println("Full params received: " + new TreeMap<>(allParams));

        String vnp_TxnRef = allParams.get("vnp_TxnRef");
        String vnp_TransactionNo = allParams.get("vnp_TransactionNo");
        String vnp_ResponseCode = allParams.get("vnp_ResponseCode");
        String secureHash = allParams.get("vnp_SecureHash");

        System.out.println("Extracted - vnp_TxnRef: " + vnp_TxnRef + ", vnp_TransactionNo: " + vnp_TransactionNo +
                ", vnp_ResponseCode: " + vnp_ResponseCode + ", secureHash: " + secureHash);

        try {
            if (vnp_TxnRef == null || vnp_TxnRef.isEmpty()) {
                System.out.println("Error: vnp_TxnRef is missing or empty");
                return ResponseEntity.badRequest().build();
            }

            System.out.println("Validating signature with secureHash: " + secureHash);
            if (!vnpayUtil.validateSignature(allParams, secureHash)) {
                System.out.println(
                        "Signature validation failed for vnp_TxnRef: " + vnp_TxnRef + ", params: " + allParams);
                return ResponseEntity.badRequest().build();
            }

            Payment payment = paymentService.getPaymentByOrderId(vnp_TxnRef)
                    .orElseThrow(() -> {
                        System.out.println("Payment not found in DB for orderId: " + vnp_TxnRef);
                        return new InvalidInputException("Payment not found for orderId: " + vnp_TxnRef);
                    });

            PaymentStatus status;
            if ("00".equals(vnp_ResponseCode)) {
                status = PaymentStatus.SUCCESS;
                System.out.println("Payment successful, setting status to: " + status);
            } else {
                status = PaymentStatus.FAILED;
                System.out.println("Payment failed or cancelled, setting status to: " + status + ", responseCode: "
                        + vnp_ResponseCode);
            }

            paymentService.updatePaymentStatus(vnp_TxnRef, vnp_TransactionNo, status, "VNPAY", vnp_ResponseCode,
                    "Payment processed", secureHash);
            System.out.println("Payment status updated to: " + status + " for orderId: " + vnp_TxnRef);

            String redirectUrl = status == PaymentStatus.SUCCESS
                    ? "http://localhost:3000/payment-success?orderId=" + vnp_TxnRef
                    : "http://localhost:3000/payment-error?error=" + URLEncoder.encode(
                            "Payment cancelled or failed with code: " + vnp_ResponseCode, StandardCharsets.UTF_8);
            System.out.println("Redirecting to: " + redirectUrl);
            return ResponseEntity.status(302)
                    .header("Location", redirectUrl)
                    .build();
        } catch (Exception e) {
            System.out.println("Callback error for orderId: " + vnp_TxnRef + ": " + e.getMessage() + ", stacktrace: "
                    + Arrays.toString(e.getStackTrace()));
            String errorRedirectUrl = "http://localhost:3000/payment-error?error="
                    + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            System.out.println("Redirecting to error: " + errorRedirectUrl);
            return ResponseEntity.status(302)
                    .header("Location", errorRedirectUrl)
                    .build();
        }
    }

    @PostMapping("/confirm-cod")
    public ResponseEntity<Map<String, String>> confirmCodPayment(@RequestBody Map<String, String> request) {
        String orderId = request.get("orderId");
        if (orderId == null || orderId.isEmpty()) {
            throw new InvalidInputException("orderId is required");
        }
        Payment payment = paymentService.confirmCodPayment(orderId);
        Map<String, String> response = new HashMap<>();
        response.put("orderId", payment.getOrderId());
        response.put("status", payment.getStatus().toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Payment> getPaymentByOrderId(@PathVariable String orderId) {
        Payment payment = paymentService.getPaymentByOrderId(orderId)
                .orElseThrow(() -> new InvalidInputException("Payment not found for orderId: " + orderId));
        return ResponseEntity.ok(payment);
    }

    // DTO cho request
    public static class PaymentRequest {
        private String orderId;
        private Long userId;
        private CartDTO cartDTO;
        private PaymentMethod paymentMethod;
        private Address shippingAddress;

        // Getters and Setters
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public CartDTO getCartDTO() { return cartDTO; }
        public void setCartDTO(CartDTO cartDTO) { this.cartDTO = cartDTO; }
        public PaymentMethod getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
        public Address getShippingAddress() { return shippingAddress; }
        public void setShippingAddress(Address shippingAddress) { this.shippingAddress = shippingAddress; }
    }
}