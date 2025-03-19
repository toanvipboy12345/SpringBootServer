
package com.ecommerce.Ecommerce.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false, unique = true)
    private String orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "cart_token", nullable = true) // Thêm trường cartToken
    private String cartToken;

    @Column(name = "total_amount", nullable = false)
    private double totalAmount;

    @Embedded
    private Address shippingAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_method_id")
    private ShippingMethod shippingMethod;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> items;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "is_coupon_applied", nullable = false)
    private boolean isCouponApplied = false;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "customer_name", nullable = true)
    private String customerName;

    @Column(name = "phone_number", nullable = true)
    private String phoneNumber;

    // Constructors
    public Order() {
        super();
    }

    public Order(String orderId, User user, String cartToken, double totalAmount, Address shippingAddress, 
                 ShippingMethod shippingMethod, String email, String customerName, String phoneNumber) {
        this();
        this.orderId = orderId;
        this.user = user;
        this.cartToken = cartToken; // Thêm cartToken vào constructor
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.shippingMethod = shippingMethod;
        this.email = email;
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getCartToken() { return cartToken; } // Getter cho cartToken
    public void setCartToken(String cartToken) { this.cartToken = cartToken; } // Setter cho cartToken
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public Address getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(Address shippingAddress) { this.shippingAddress = shippingAddress; }
    public ShippingMethod getShippingMethod() { return shippingMethod; }
    public void setShippingMethod(ShippingMethod shippingMethod) { this.shippingMethod = shippingMethod; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public boolean isCouponApplied() { return isCouponApplied; }
    public void setCouponApplied(boolean isCouponApplied) { this.isCouponApplied = isCouponApplied; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}