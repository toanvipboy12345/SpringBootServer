package com.ecommerce.Ecommerce.model.dto;

import com.ecommerce.Ecommerce.model.Auditable;
import com.ecommerce.Ecommerce.model.OrderStatus;
import com.ecommerce.Ecommerce.model.PaymentMethod;
import com.ecommerce.Ecommerce.model.PaymentStatus;

import java.util.List;

public class InvoiceDTO extends Auditable {

    private String orderId;
    private String customerIdentifier;
    private String customerName;
    private String email;
    private String phoneNumber;
    private AddressDTO shippingAddress;
    private String shippingMethod;
    private double totalAmount;
    private double shippingFee;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private OrderStatus status;
    private String transactionId; // Thêm trường transactionId
    private List<InvoiceItemDTO> items;

    // Nested DTO cho Address
    public static class AddressDTO {
        private String street;
        private String ward;
        private String district;
        private String city;
        private String country;

        public AddressDTO() {}

        public AddressDTO(String street, String ward, String district, String city, String country) {
            this.street = street;
            this.ward = ward;
            this.district = district;
            this.city = city;
            this.country = country;
        }

        // Getters and Setters
        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }
        public String getWard() { return ward; }
        public void setWard(String ward) { this.ward = ward; }
        public String getDistrict() { return district; }
        public void setDistrict(String district) { this.district = district; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
    }

    // Nested DTO cho InvoiceItem
    public static class InvoiceItemDTO {
        private Long productId;
        private String productName;
        private String variantColor;
        private String size;
        private Integer quantity;
        private double price;
        private String mainImage;

        public InvoiceItemDTO() {}

        public InvoiceItemDTO(Long productId, String productName, String variantColor, String size, 
                             Integer quantity, double price, String mainImage) {
            this.productId = productId;
            this.productName = productName;
            this.variantColor = variantColor;
            this.size = size;
            this.quantity = quantity;
            this.price = price;
            this.mainImage = mainImage;
        }

        // Getters and Setters
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public String getVariantColor() { return variantColor; }
        public void setVariantColor(String variantColor) { this.variantColor = variantColor; }
        public String getSize() { return size; }
        public void setSize(String size) { this.size = size; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
        public String getMainImage() { return mainImage; }
        public void setMainImage(String mainImage) { this.mainImage = mainImage; }
    }

    // Constructor
    public InvoiceDTO() {
        super();
    }

    public InvoiceDTO(String orderId, String customerIdentifier, String customerName, String email,
                     String phoneNumber, AddressDTO shippingAddress, String shippingMethod, double totalAmount,
                     double shippingFee, PaymentMethod paymentMethod, PaymentStatus paymentStatus, OrderStatus status,
                     String transactionId, List<InvoiceItemDTO> items) {
        super();
        this.orderId = orderId;
        this.customerIdentifier = customerIdentifier;
        this.customerName = customerName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.shippingAddress = shippingAddress;
        this.shippingMethod = shippingMethod;
        this.totalAmount = totalAmount;
        this.shippingFee = shippingFee;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.status = status;
        this.transactionId = transactionId; // Khởi tạo transactionId
        this.items = items;
    }

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getCustomerIdentifier() { return customerIdentifier; }
    public void setCustomerIdentifier(String customerIdentifier) { this.customerIdentifier = customerIdentifier; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public AddressDTO getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(AddressDTO shippingAddress) { this.shippingAddress = shippingAddress; }
    public String getShippingMethod() { return shippingMethod; }
    public void setShippingMethod(String shippingMethod) { this.shippingMethod = shippingMethod; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public double getShippingFee() { return shippingFee; }
    public void setShippingFee(double shippingFee) { this.shippingFee = shippingFee; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public String getTransactionId() { return transactionId; } // Getter cho transactionId
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; } // Setter cho transactionId
    public List<InvoiceItemDTO> getItems() { return items; }
    public void setItems(List<InvoiceItemDTO> items) { this.items = items; }
}