package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; 
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name); // Phương thức tìm kiếm danh mục theo tên
    List<Category> findByNameContainingIgnoreCase(String name);
}
