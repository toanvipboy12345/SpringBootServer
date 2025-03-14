package com.ecommerce.Ecommerce.model;

import jakarta.persistence.*;

@Entity
@Table(name = "shipping_methods")
public class ShippingMethod extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "shipping_fee", nullable = false)
    private double shippingFee;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ShippingMethodStatus status;

    // Constructors
    public ShippingMethod() {
        super();
    }

    public ShippingMethod(String code, String name, double shippingFee, ShippingMethodStatus status) {
        super();
        this.code = code;
        this.name = name;
        this.shippingFee = shippingFee;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getShippingFee() { return shippingFee; }
    public void setShippingFee(double shippingFee) { this.shippingFee = shippingFee; }
    public ShippingMethodStatus getStatus() { return status; }
    public void setStatus(ShippingMethodStatus status) { this.status = status; }
}

