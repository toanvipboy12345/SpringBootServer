package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.model.Product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByCode(String code);

    List<Product> findByCodeContainingOrNameContaining(String code, String name);

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByBrandId(Long brandId);

    List<Product> findByCategoryIdAndBrandId(Long categoryId, Long brandId);

    List<Product> findByCodeContainingOrNameContainingAndCategoryId(String code, String name, Long categoryId);

    List<Product> findByCodeContainingOrNameContainingAndBrandId(String code, String name, Long brandId);

    List<Product> findByCodeContainingOrNameContainingAndCategoryIdAndBrandId(String code, String name, Long categoryId, Long brandId);
}