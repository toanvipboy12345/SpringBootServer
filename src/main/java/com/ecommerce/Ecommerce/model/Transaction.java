package com.ecommerce.Ecommerce.model;

import jakarta.persistence.*;

@Entity
@Table(name = "transactions")
public class Transaction extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private String orderId; // Liên kết với Payment

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod; // MOMO, VNPAY, COD

    @Column(name = "amount", nullable = false)
    private double amount; // Số tiền giao dịch

    @Column(name = "trans_id", nullable = true)
    private String transId; // ID giao dịch từ cổng (null nếu COD)

    @Column(name = "pay_type")
    private String payType; // Loại thanh toán (tùy cổng)

    @Column(name = "response_code")
    private String responseCode; // Mã phản hồi từ cổng

    @Column(name = "message")
    private String message; // Thông điệp từ cổng

    @Column(name = "response_time")
    private String responseTime; // Thời gian phản hồi

    @Column(name = "signature")
    private String signature; // Chữ ký từ cổng (xác thực)

    // Constructors
    public Transaction() {
        super();
    }

    public Transaction(String orderId, String paymentMethod, double amount, String transId, String payType, String responseCode, String message, String responseTime, String signature) {
        super();
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.transId = transId;
        this.payType = payType;
        this.responseCode = responseCode;
        this.message = message;
        this.responseTime = responseTime;
        this.signature = signature;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getTransId() { return transId; }
    public void setTransId(String transId) { this.transId = transId; }
    public String getPayType() { return payType; }
    public void setPayType(String payType) { this.payType = payType; }
    public String getResponseCode() { return responseCode; }
    public void setResponseCode(String responseCode) { this.responseCode = responseCode; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getResponseTime() { return responseTime; }
    public void setResponseTime(String responseTime) { this.responseTime = responseTime; }
    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }
}
