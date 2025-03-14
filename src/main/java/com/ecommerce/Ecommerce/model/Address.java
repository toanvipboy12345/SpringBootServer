package com.ecommerce.Ecommerce.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class Address {
    private String street; // Số nhà và đường
    private String ward;   // Phường/xã
    private String district; // Quận/huyện
    private String city;   // Thành phố/tỉnh
    private String country; // Quốc gia

    // Getters and Setters
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}