package com.ecommerce.Ecommerce.model;

import jakarta.persistence.*;

@Entity
@Table(name = "payments")
public class Payment extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private String orderId; // ID của đơn hàng liên kết

    @Column(name = "cart_token", nullable = true)
    private String cartToken; // Hỗ trợ khách chưa đăng nhập

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user; // Liên kết với User nếu khách đã đăng nhập

    @Column(name = "amount", nullable = false)
    private double amount; // Số tiền thanh toán

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod; // Phương thức thanh toán

    @Column(name = "transaction_id")
    private String transactionId; // ID giao dịch từ cổng thanh toán (online)

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status; // Trạng thái thanh toán

    @Column(name = "request_data", columnDefinition = "TEXT")
    private String requestData; // Dữ liệu gửi đến cổng thanh toán (JSON)

    // Constructors
    public Payment() {
        super();
    }

    public Payment(String orderId, String cartToken, User user, double amount, PaymentMethod paymentMethod, String transactionId, PaymentStatus status, String requestData) {
        super();
        this.orderId = orderId;
        this.cartToken = cartToken;
        this.user = user;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.status = status;
        this.requestData = requestData;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getCartToken() { return cartToken; }
    public void setCartToken(String cartToken) { this.cartToken = cartToken; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public String getRequestData() { return requestData; }
    public void setRequestData(String requestData) { this.requestData = requestData; }
}