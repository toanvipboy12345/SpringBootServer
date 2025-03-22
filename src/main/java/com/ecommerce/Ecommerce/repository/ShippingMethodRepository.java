package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.model.ShippingMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShippingMethodRepository extends JpaRepository<ShippingMethod, Long> {
    Optional<ShippingMethod> findByCode(String code);
    long count();
}