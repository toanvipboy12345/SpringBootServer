package com.ecommerce.Ecommerce.model.dto;

import com.ecommerce.Ecommerce.model.Auditable;
import com.ecommerce.Ecommerce.model.OrderStatus;
import com.ecommerce.Ecommerce.model.PaymentMethod;
import com.ecommerce.Ecommerce.model.PaymentStatus;

import java.util.List;

public class InvoiceDTO extends Auditable {

    private String orderId; // Mã đơn hàng (orderId) - định danh duy nhất
    private String customerIdentifier; // Định danh khách hàng (cartToken hoặc userId dưới dạng chuỗi)
    private String customerName; // Tên khách hàng
    private String email; // Email khách hàng
    private String phoneNumber; // Số điện thoại khách hàng
    private AddressDTO shippingAddress; // Địa chỉ giao hàng
    private String shippingMethod; // Phương thức vận chuyển
    private double totalAmount; // Tổng tiền (lấy trực tiếp từ Order)
    private double shippingFee; // Phí vận chuyển (lấy trực tiếp từ Order hoặc ShippingMethod)
    private PaymentMethod paymentMethod; // Phương thức thanh toán
    private PaymentStatus paymentStatus; // Trạng thái thanh toán
    private OrderStatus status; // Trạng thái đơn hàng
    private List<InvoiceItemDTO> items; // Danh sách các mục trong hóa đơn

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
        private String variantColor; // Màu sắc của biến thể
        private String size; // Kích thước
        private Integer quantity; // Số lượng
        private double price; // Giá cuối cùng của sản phẩm (lấy trực tiếp từ OrderItem.price)

        public InvoiceItemDTO() {}

        public InvoiceItemDTO(Long productId, String productName, String variantColor, String size, 
                            Integer quantity, double price) {
            this.productId = productId;
            this.productName = productName;
            this.variantColor = variantColor;
            this.size = size;
            this.quantity = quantity;
            this.price = price; // Giá này được lấy trực tiếp từ OrderItem.price
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
    }

    // Constructor
    public InvoiceDTO() {
        super();
    }

    public InvoiceDTO(String orderId, String customerIdentifier, String customerName, String email,
                     String phoneNumber, AddressDTO shippingAddress, String shippingMethod, double totalAmount,
                     double shippingFee, PaymentMethod paymentMethod, PaymentStatus paymentStatus, OrderStatus status,
                     List<InvoiceItemDTO> items) {
        super();
        this.orderId = orderId;
        this.customerIdentifier = customerIdentifier;
        this.customerName = customerName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.shippingAddress = shippingAddress;
        this.shippingMethod = shippingMethod;
        this.totalAmount = totalAmount; // Lấy trực tiếp từ Order
        this.shippingFee = shippingFee; // Lấy trực tiếp từ Order hoặc ShippingMethod
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.status = status;
        this.items = items;
    }

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getCustomerIdentifier() { return customerIdentifier; } // Định danh khách hàng (cartToken hoặc userId)
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
    public double getTotalAmount() { return totalAmount; } // Lấy trực tiếp từ Order
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public double getShippingFee() { return shippingFee; } // Lấy trực tiếp từ Order hoặc ShippingMethod
    public void setShippingFee(double shippingFee) { this.shippingFee = shippingFee; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public List<InvoiceItemDTO> getItems() { return items; }
    public void setItems(List<InvoiceItemDTO> items) { this.items = items; }
}