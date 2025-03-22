package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    // Tìm kiếm thương hiệu dựa trên tên chính xác
    Optional<Brand> findByName(String name);

    // Tìm kiếm thương hiệu theo tên chứa chuỗi tìm kiếm, không phân biệt chữ hoa/chữ thường
    List<Brand> findByNameContainingIgnoreCase(String name);
    long count();
}
