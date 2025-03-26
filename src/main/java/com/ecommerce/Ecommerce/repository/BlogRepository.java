package com.ecommerce.Ecommerce.repository;

import com.ecommerce.Ecommerce.model.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
}