package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.model.Product;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByCode(String code);

    List<Product> findByCodeContainingOrNameContaining(String code, String name, Sort sort);

    List<Product> findByCodeContainingOrNameContainingAndCategoryId(String code, String name, Long categoryId, Sort sort);

    List<Product> findByCodeContainingOrNameContainingAndBrandId(String code, String name, Long brandId, Sort sort);

    List<Product> findByCodeContainingOrNameContainingAndCategoryIdAndBrandId(String code, String name, Long categoryId, Long brandId, Sort sort);

    List<Product> findByCategoryId(Long categoryId, Sort sort);

    List<Product> findByBrandId(Long brandId, Sort sort);

    List<Product> findByCategoryIdAndBrandId(Long categoryId, Long brandId, Sort sort);

    List<Product> findAll(Sort sort);
    long count();
}