package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCode(String code);
    long count();
}